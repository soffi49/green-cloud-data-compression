import {
    AGENTS_REPORTS_STATE,
    AGENTS_STATE,
    CLIENTS_REPORTS_STATE,
    CLIENTS_STATE,
    GRAPH_STATE,
    MANAGING_SYSTEM_STATE,
    NETWORK_REPORTS_STATE,
    NETWORK_STATE,
    resetAgentsReportsState,
    resetAgentsState,
    resetClientsReportsState,
    resetClientsState,
    resetGraphState,
    resetManagingSystemReportsState,
    resetManagingSystemState,
    resetNetworkReportsState,
    resetNetworkState,
    resetSimulationState,
    SIMULATION_STATE
} from "../module"

const resetSystemState = () => {
    resetAgentsReportsState()
    resetAgentsState()
    resetGraphState()
    resetClientsReportsState()
    resetClientsState()
    resetManagingSystemState()
    resetManagingSystemReportsState()
    resetNetworkReportsState()
    resetNetworkState()
    resetSimulationState()
}

const getSystemState = () => {
    return ({
        systemStartTime: SIMULATION_STATE.systemStartTime,
        secondsPerHour: SIMULATION_STATE.systemStartTime,
        network: { ...NETWORK_STATE },
        agents: {
            ...CLIENTS_STATE,
            ...AGENTS_STATE
        },
        managingSystem: { ...MANAGING_SYSTEM_STATE },
        graph: { ...GRAPH_STATE }
    })
}

const getAgentsState = () => {
    return ({
        ...AGENTS_STATE,
        graph: { ...GRAPH_STATE }
    })
}

const getGraphState = () => {
    return ({
        ...GRAPH_STATE
    })
}

const getClientsState = () => {
    return ({
        clients: CLIENTS_STATE.clients.map(client => ({ name: client.name, status: client.status, isSplit: client.isSplit })),
    })
}

const getClient = (clientName: string) => {
    return CLIENTS_STATE.clients.filter(client => client.name.toUpperCase() === clientName.toUpperCase())[0] ?? null
}

const getAgent = (agentName: string) => {
    return AGENTS_STATE.agents.filter(agent => agent.name.toUpperCase() === agentName.toUpperCase())[0] ?? null
}

const getManagingState = () => {
    return ({
        managing: { ...MANAGING_SYSTEM_STATE },
    })
}

const getNetworkState = () => {
    return ({
        network: { ...NETWORK_STATE },
        simulation: { ...SIMULATION_STATE }
    })
}


const getReportsState = () => {
    return ({
        ...NETWORK_REPORTS_STATE,
        ...CLIENTS_REPORTS_STATE,
        ...AGENTS_REPORTS_STATE
    })
}

export {
    resetSystemState,
    getSystemState,
    getAgentsState,
    getGraphState,
    getClientsState,
    getClient,
    getAgent,
    getManagingState,
    getNetworkState,
    getReportsState
}