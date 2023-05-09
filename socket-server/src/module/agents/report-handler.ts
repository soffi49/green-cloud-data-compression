import { AGENT_TYPES } from "../../constants/index.js"
import { getCurrentTime } from "../../utils/index.js"
import { AGENTS_REPORTS_STATE, AGENTS_STATE } from "./agents-state.js"

const changeCloudNetworkCapacityEvent = (cnaName, serverName, capacity, isAdded) => {
    const events = AGENTS_REPORTS_STATE.agentsReports.filter(agentReport => agentReport.name === cnaName)[0]?.events

    if (events) {
        const eventName = isAdded ? 'New Server' : 'Server disabled'
        const event = isAdded ? `added to ${cnaName}` : `disabled from ${cnaName}`
        const eventDescription = `Server ${serverName} with capacity ${capacity} was ${event}`

        events.push({
            type: 'AGENT_CONNECTION_CHANGE',
            time: getCurrentTime(),
            name: eventName,
            description: eventDescription
        })
    }
}

const reportSystemTraffic = (time) => {
    const currentState = AGENTS_STATE.agents
        .filter(agent => agent.type === AGENT_TYPES.CLOUD_NETWORK)
        .reduce((sum, agent) => {
            sum.capacity = sum.capacity + agent.maximumCapacity
            sum.traffic = sum.traffic + agent.maximumCapacity * agent.traffic
            return sum
        }, ({ capacity: 0, traffic: 0 }))
    const currentTraffic = currentState.capacity === 0 ? 0 : currentState.traffic / currentState.capacity

    return ({ time, value: currentTraffic })
}

const reportSchedulerData = (agent, time) => {
    const reports = AGENTS_REPORTS_STATE.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    const queueCapacity = agent.maxQueueSize === 0 ? 0 : agent.scheduledJobs.length / agent.maxQueueSize

    reports.deadlinePriorityReport.push({ time, value: agent.deadlinePriority })
    reports.powerPriorityReport.push({ time, value: agent.powerPriority })
    reports.queueCapacityReport.push({ time, value: queueCapacity })
}

const reportCloudNetworkData = (agent, time) => {
    const reports = AGENTS_REPORTS_STATE.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    reports.clientsReport.push({ time, value: agent.totalNumberOfClients })
    reports.capacityReport.push({ time, value: agent.maximumCapacity })
    reports.trafficReport.push({ time, value: agent.traffic })
    reports.successRatioReport.push({ time, value: agent.successRatio })
}

const reportServerData = (agent, time) => {
    const reports = AGENTS_REPORTS_STATE.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    reports.trafficReport.push({ time, value: agent.traffic })
    reports.capacityReport.push({ time, value: agent.currentMaximumCapacity })
    reports.greenPowerUsageReport.push({ time, value: agent.traffic })
    reports.greenPowerUsageReport.push({ time, value: agent.currentMaximumCapacity * agent.backUpTraffic })
    reports.successRatioReport.push({ time, value: agent.successRatio })
}

const reportGreenSourceData = (agent, time) => {
    const reports = AGENTS_REPORTS_STATE.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    reports.trafficReport.push({ time, value: agent.traffic })
    reports.availableGreenPowerReport.push({ time, value: agent.availableGreenEnergy })
    reports.capacityReport.push({ time, value: agent.currentMaximumCapacity })
    reports.jobsOnGreenPowerReport.push({ time, value: agent.numberOfExecutedJobs })
    reports.jobsOnHoldReport.push({ time, value: agent.numberOfJobsOnHold })
    reports.successRatioReport.push({ time, value: agent.successRatio })
}

const updateAgentsReportsState = (time) => {
    AGENTS_STATE.agents.forEach(agent => {
        if (agent.type === AGENT_TYPES.CLOUD_NETWORK) {
            reportCloudNetworkData(agent, time)
        }
        else if (agent.type === AGENT_TYPES.SERVER) {
            reportServerData(agent, time)
        }
        else if (agent.type === AGENT_TYPES.GREEN_ENERGY) {
            reportGreenSourceData(agent, time)
        }
        else if (agent.type === AGENT_TYPES.SCHEDULER) {
            reportSchedulerData(agent, time)
        }
    })
}

export {
    changeCloudNetworkCapacityEvent,
    updateAgentsReportsState
}