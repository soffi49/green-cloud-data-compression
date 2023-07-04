import { AGENT_TYPES } from "../../constants/constants"
import { createAgentConnections, createEdge, createNodeForAgent, getAgentByName, getAgentNodeById, getNodeState, registerAgent } from "../../utils"
import { CLIENTS_STATE } from "../clients"
import { GRAPH_STATE } from "../graph/graph-state"
import { AGENTS_STATE } from "./agents-state"
import { changeCloudNetworkCapacityEvent } from "./report-handler"

const getNewTraffic = (maximumCapacity, powerInUse) => maximumCapacity === 0 ? 0 : powerInUse / maximumCapacity * 100

const getNewCloudNetworkTraffic = (agent, powerInUse) => {
    agent.isActive = powerInUse > 0
    agent.traffic = getNewTraffic(agent.maximumCapacity, powerInUse)

    const connection = GRAPH_STATE.connections.find(el => el.data.source === agent.name)

    if (connection) {
        connection.state = agent.isActive ? 'active' : 'inactive'
    }
}

const addGreenSourcesToServer = (data) => {
    AGENTS_STATE.agents
        .filter(el => el.type === AGENT_TYPES.SERVER && el.name === data.serverAgent && !el.greenEnergyAgents.includes(data.name))
        .forEach(server => server.greenEnergyAgents.push(data.name))
}

const addServersToCNA = (data) => {
    AGENTS_STATE.agents
        .filter(el => el.type === AGENT_TYPES.CLOUD_NETWORK && el.name === data.cloudNetworkAgent && !el.serverAgents.includes(data.name))
        .forEach(cna => cna.serverAgents.push(data.name))
}

const handleSetMaximumCapacity = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const node = getAgentNodeById(GRAPH_STATE.nodes, msg.agentName)
    const { maximumCapacity, powerInUse } = msg.data

    if (agent) {
        if (agent.type === AGENT_TYPES.CLOUD_NETWORK) {
            agent.maximumCapacity = maximumCapacity
            getNewCloudNetworkTraffic(agent, powerInUse)
        } else {
            agent.currentMaximumCapacity = maximumCapacity
            agent.traffic = getNewTraffic(maximumCapacity, powerInUse)
        }

        if (node) {
            node.state = getNodeState(agent)
        }
    }
}

const handleSetTraffic = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const node = getAgentNodeById(GRAPH_STATE.nodes, msg.agentName)
    const powerInUse = msg.data

    if (agent) {
        if (agent.type === AGENT_TYPES.CLOUD_NETWORK) {
            getNewCloudNetworkTraffic(agent, powerInUse)
        } else {
            agent.traffic = getNewTraffic(agent.currentMaximumCapacity, powerInUse)
        }
        if (node) {
            node.state = getNodeState(agent)
        }
    }
}

const handleSetBackUpTraffic = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const node = getAgentNodeById(GRAPH_STATE.nodes, msg.agentName)
    const backUpPower = msg.data

    if (agent) {
        agent.backUpTraffic = getNewTraffic(agent.currentMaximumCapacity, backUpPower)

        if (node) {
            node.state = getNodeState(agent)
        }
    }
}

const handleSetActive = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const node = getAgentNodeById(GRAPH_STATE.nodes, msg.agentName)

    if (agent) {
        agent.isActive = msg.data

        if (node) {
            node.state = getNodeState(agent)
        }

        GRAPH_STATE.connections
            .forEach(connection => {
                if (connection.data.source === agent.name || connection.data.target === agent.name) {
                    const secondAgent = connection.data.source === agent.name ?
                        getAgentByName(AGENTS_STATE.agents, connection.data.target) :
                        getAgentByName(AGENTS_STATE.agents, connection.data.source)
                    connection.state = agent.isActive && secondAgent.isActive ? 'active' : 'inactive'
                }
            })
    }
}

const handleSetJobsOnHold = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const node = getAgentNodeById(GRAPH_STATE.nodes, msg.agentName)

    if (agent) {
        agent.numberOfJobsOnHold = msg.data

        if (node) {
            node.state = getNodeState(agent)
        }
    }
}

const handleSetJobsCount = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const node = getAgentNodeById(GRAPH_STATE.nodes, msg.agentName)
    const jobsCount = msg.data

    if (agent) {
        if (agent.type === AGENT_TYPES.SERVER || agent?.type === AGENT_TYPES.GREEN_ENERGY) {
            agent.numberOfExecutedJobs = jobsCount
        } else if (agent.type === AGENT_TYPES.CLOUD_NETWORK) {
            agent.totalNumberOfExecutedJobs = jobsCount
        }

        if (node) {
            node.state = getNodeState(agent)
        }
    }
}

const handleSetSuccessRatio = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const successRatio = msg.data

    if (agent) {
        if (agent?.type === AGENT_TYPES.SERVER ||
            agent?.type === AGENT_TYPES.GREEN_ENERGY ||
            agent?.type === AGENT_TYPES.CLOUD_NETWORK) {
            agent.successRatio = successRatio
        }
    }
}

const handleSetClientNumber = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const clientNumber = msg.data

    if (agent) {
        agent.totalNumberOfClients = clientNumber
    }
}

const handleWeatherPredictionError = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)
    const error = msg.data

    if (agent && agent.type === AGENT_TYPES.GREEN_ENERGY) {
        agent.weatherPredictionError = error
    }
}

const handleUpdateJobQueue = (msg) => {
    const agent = AGENTS_STATE.agents.find(agent => agent.type === AGENT_TYPES.SCHEDULER)

    if (agent && agent.type === AGENT_TYPES.SCHEDULER) {
        agent.scheduledJobs = msg.data.map(job => ({ clientName: (job.clientIdentifier as string).split("@")[0], jobId: job.jobId }))
    }
}

const handleUpdatePowerPriority = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)

    if (agent && agent.type === AGENT_TYPES.SCHEDULER) {
        agent.powerPriority = msg.data
    }
}

const handleUpdateDeadlinePriority = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)

    if (agent && agent.type === AGENT_TYPES.SCHEDULER) {
        agent.deadlinePriority = msg.data
    }
}

const handleRemoveAgent = (msg) => {
    const agentName = msg.agentName

    AGENTS_STATE.agents = AGENTS_STATE.agents.filter(agent => agent.name !== agentName)
    GRAPH_STATE.nodes = GRAPH_STATE.nodes.filter(node => node.id !== agentName)
    GRAPH_STATE.connections =
        GRAPH_STATE.connections.filter(edge => edge.data.target !== agentName && edge.data.source !== agentName)
}

const handleUpdateGreenEnergy = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)

    if (agent && agent.type === AGENT_TYPES.GREEN_ENERGY) {
        agent.availableGreenEnergy = msg.data
    }
}

const handleServerDisabling = (msg) => changeCloudNetworkCapacityEvent(msg.cna, msg.server, msg.capacity, false, false)
const handleServerEnabling = (msg) => changeCloudNetworkCapacityEvent(msg.cna, msg.server, msg.capacity, true, false)

const handleRegisterAgent = (msg) => {
    const agentType = msg.agentType
    const registerData = msg.data

    if (!getAgentByName(AGENTS_STATE.agents, registerData.name)) {
        const newAgent = registerAgent(registerData, agentType)

        if (newAgent) {
            if (agentType === AGENT_TYPES.CLIENT) {
                CLIENTS_STATE.clients.push(newAgent)
            }
            else {
                AGENTS_STATE.agents.push(newAgent)
                GRAPH_STATE.nodes.push(createNodeForAgent(newAgent))
                Object.assign(GRAPH_STATE.connections, GRAPH_STATE.connections.concat((createAgentConnections(newAgent))))
            }
        }
    }
}

const handleUpdateServerConnection = (msg) => {
    const agent = getAgentByName(AGENTS_STATE.agents, msg.agentName)

    if (agent) {
        const { isConnected, serverName } = msg.data

        if (isConnected) {
            agent.connectedServers.push(serverName)
            Object.assign(GRAPH_STATE.connections, GRAPH_STATE.connections.concat(createEdge(agent.name, serverName)))
        } else {
            agent.connectedServers = agent.connectedServers.filter(server => server !== serverName)
            GRAPH_STATE.connections = GRAPH_STATE.connections.filter(edge => edge.data.id !== [agent.name, serverName, 'BI'].join('-'))
        }
    }
}

export {
    handleSetMaximumCapacity,
    handleSetTraffic,
    handleSetBackUpTraffic,
    handleSetActive,
    handleSetJobsOnHold,
    handleSetJobsCount,
    handleSetSuccessRatio,
    handleSetClientNumber,
    handleWeatherPredictionError,
    handleUpdateJobQueue,
    handleUpdatePowerPriority,
    handleUpdateDeadlinePriority,
    handleRemoveAgent,
    handleUpdateGreenEnergy,
    handleServerDisabling,
    addGreenSourcesToServer,
    addServersToCNA,
    handleRegisterAgent,
    handleUpdateServerConnection,
    handleServerEnabling
}