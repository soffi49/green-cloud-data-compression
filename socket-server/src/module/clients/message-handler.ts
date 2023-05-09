import { JOB_STATUSES } from "../../constants/constants"
import { getAgentByName } from "../../utils/agent-utils"
import { CLIENTS_STATE } from "./clients-state"

const handleSetClientJobStatus = (msg) => {
    const agent = getAgentByName(CLIENTS_STATE.clients, msg.agentName)
    const jobStatus = msg.data.status
    const splitJobId = msg.data.splitJobId

    if (agent) {
        if (jobStatus === JOB_STATUSES.FAILED) {
            agent.status = jobStatus
            if (agent.isSplit) {
                agent.splitJobs.forEach(job => job.status = jobStatus)
            }
            return
        }
        if (splitJobId) {
            const splitJob = agent.splitJobs.find(job => job.splitJobId === splitJobId)
            if (splitJob) {
                splitJob.status = jobStatus
            }
        } else {
            agent.status = jobStatus
        }
    }
}

const handleSetClientJobTimeFrame = (msg) => {
    const agent = getAgentByName(CLIENTS_STATE.clients, msg.agentName)
    const { start, end } = msg.data
    const splitJobId = msg.data.splitJobId

    if (agent) {
        if (splitJobId) {
            const splitJob = agent.splitJobs.find(job => job.splitJobId === splitJobId)
            if (splitJob) {
                splitJob.start = start
                splitJob.end = end
            }
        } else {
            agent.job.start = start
            agent.job.end = end
        }
    }
}

const handleSetClientJobDurationMap = (msg) => {
    const agent = getAgentByName(CLIENTS_STATE.clients, msg.agentName)

    if (agent) {
        agent.durationMap = msg.data
    }
}

const handleJobSplit = (msg) => {
    const clients = CLIENTS_STATE.clients
    const clientForSplit = clients.find(client => client.job.jobId === msg.jobId)

    if (clientForSplit) {
        const splitData = msg.data.map(splitJob => ({ status: JOB_STATUSES.CREATED, ...splitJob }))
        clientForSplit.isSplit = true
        clientForSplit.splitJobs = splitData
    }
}


export {
    handleSetClientJobStatus,
    handleSetClientJobTimeFrame,
    handleSetClientJobDurationMap,
    handleJobSplit
}