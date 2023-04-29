const { AGENT_TYPES, JOB_STATUSES } = require("../constants/constants")

const getCurrentTime = (state) => {
    const timeDiff = new Date().getTime() - state.systemStartTime
    const timeMultiplier = 3600 / state.secondsPerHour;
    const realTimeDifference = timeDiff * timeMultiplier;
    return state.systemStartTime + Math.round(realTimeDifference)
}

const reportFailedJob = (state, time) => ({ time, value: state.network.failedJobsNo })
const reportFinishedJob = (state, time) => ({ time, value: state.network.finishedJobsNo })
const reportClients = (state, time) => ({ time, value: state.network.currClientsNo })

const reportSystemTraffic = (state, time) => {
    const currentState = state.agents.agents
        .filter(agent => agent.type === AGENT_TYPES.CLOUD_NETWORK)
        .reduce((sum, agent) => {
            sum.capacity = sum.capacity + agent.maximumCapacity
            sum.traffic = sum.traffic + agent.maximumCapacity * agent.traffic
            return sum
        }, ({ capacity: 0, traffic: 0 }))
    const currentTraffic = currentState.capacity === 0 ? 0 : currentState.traffic / currentState.capacity

    return ({ time, value: currentTraffic })
}

const reportExecutedJob = (state, time) => {
    const activeStatuses = [JOB_STATUSES.IN_PROGRESS, JOB_STATUSES.ON_BACK_UP, JOB_STATUSES.ON_HOLD]
    const jobsNo = state.agents.clients.filter(client => activeStatuses.includes(client.status)).length

    return ({ time, value: jobsNo })
}

const reportJobSizeData = (state, time) => {
    const activeStatuses = [JOB_STATUSES.IN_PROGRESS, JOB_STATUSES.ON_BACK_UP, JOB_STATUSES.ON_HOLD]
    const jobSizes = state.agents.clients.filter(client => activeStatuses.includes(client.status)).map(client => parseInt(client.job.power))
    const isEmpty = jobSizes.length === 0
    const avg = !isEmpty ? jobSizes.reduce((size1, size2) => size1 + size2, 0) / jobSizes.length : 0

    return ({ time, avg, min: isEmpty ? 0 : Math.min(...jobSizes), max: isEmpty ? 0 : Math.max(...jobSizes) })
}

const reportSchedulerData = (agent, reportsState, time) => {
    const reports = reportsState.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    const queueCapacity = agent.maxQueueSize === 0 ? 0 : agent.scheduledJobs.length / agent.maxQueueSize

    reports.deadlinePriorityReport.push({ time, value: agent.deadlinePriority })
    reports.powerPriorityReport.push({ time, value: agent.powerPriority })
    reports.queueCapacityReport.push({ time, value: queueCapacity })
}

const reportCloudNetworkData = (agent, reportsState, time) => {
    const reports = reportsState.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    reports.clientsReport.push({ time, value: agent.totalNumberOfClients })
    reports.capacityReport.push({ time, value: agent.maximumCapacity })
    reports.trafficReport.push({ time, value: agent.traffic })
    reports.successRatioReport.push({ time, value: agent.successRatio })
}

const reportServerData = (agent, reportsState, time) => {
    const reports = reportsState.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    reports.trafficReport.push({ time, value: agent.traffic })
    reports.capacityReport.push({ time, value: agent.currentMaximumCapacity })
    reports.greenPowerUsageReport.push({ time, value: agent.traffic })
    reports.greenPowerUsageReport.push({ time, value: agent.currentMaximumCapacity * agent.backUpTraffic })
    reports.successRatioReport.push({ time, value: agent.successRatio })
}

const reportGreenSourceData = (agent, reportsState, time) => {
    const reports = reportsState.agentsReports.filter(agentReport => agentReport.name === agent.name)[0].reports

    reports.trafficReport.push({ time, value: agent.traffic })
    reports.availableGreenPowerReport.push({ time, value: agent.availableGreenEnergy })
    reports.capacityReport.push({ time, value: agent.currentMaximumCapacity })
    reports.jobsOnGreenPowerReport.push({ time, value: agent.numberOfExecutedJobs })
    reports.jobsOnHoldReport.push({ time, value: agent.numberOfJobsOnHold })
    reports.successRatioReport.push({ time, value: agent.successRatio })
}

const getAgentsReportState = (state, reportsState, time) => {
    state.agents.agents.forEach(agent => {
        if (agent.type === AGENT_TYPES.CLOUD_NETWORK) {
            reportCloudNetworkData(agent, reportsState, time)
        }
        else if (agent.type === AGENT_TYPES.SERVER) {
            reportServerData(agent, reportsState, time)
        }
        else if (agent.type === AGENT_TYPES.GREEN_ENERGY) {
            reportGreenSourceData(agent, reportsState, time)
        }
        else if (agent.type === AGENT_TYPES.SCHEDULER) {
            reportSchedulerData(agent, reportsState, time)
        }
    })
}

const getNewReportsState = (state, reportsState) => {
    const currTime = getCurrentTime(state)
    const jobSizeData = reportJobSizeData(state, currTime)

    const failJobsReport = reportsState.failJobsReport.concat(reportFailedJob(state, currTime))
    const finishJobsReport = reportsState.finishJobsReport.concat(reportFinishedJob(state, currTime))
    const systemTrafficReport = reportsState.systemTrafficReport.concat(reportSystemTraffic(state, currTime))
    const executedJobsReport = reportsState.executedJobsReport.concat(reportExecutedJob(state, currTime))
    const clientsReport = reportsState.clientsReport.concat(reportClients(state, currTime))
    const avgJobSizeReport = reportsState.avgJobSizeReport.concat({ time: jobSizeData.time, value: jobSizeData.avg })
    const minJobSizeReport = reportsState.minJobSizeReport.concat({ time: jobSizeData.time, value: jobSizeData.min })
    const maxJobSizeReport = reportsState.maxJobSizeReport.concat({ time: jobSizeData.time, value: jobSizeData.max })

    getAgentsReportState(state, reportsState, currTime)

    return ({ failJobsReport, finishJobsReport, systemTrafficReport, executedJobsReport, clientsReport, avgJobSizeReport, minJobSizeReport, maxJobSizeReport })
}

const changeCloudNetworkCapacityEvent = (state, reportsState, cnaName, serverName, capacity, isAdded) => {
    const events = reportsState.agentsReports.filter(agentReport => agentReport.name === cnaName)[0]?.events

    if (events) {
        const eventName = isAdded ? 'New Server' : 'Server disabled'
        const event = isAdded ? `added to ${cnaName}` : `disabled from ${cnaName}`
        const eventDescription = `Server ${serverName} with capacity ${capacity} was ${event}`

    
        events.push({
            type: 'AGENT_CONNECTION_CHANGE',
            time: getCurrentTime(state),
            name: eventName,
            description: eventDescription
        })
    }
}

module.exports = {
    getNewReportsState,
    changeCloudNetworkCapacityEvent
}