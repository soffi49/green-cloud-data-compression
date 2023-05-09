import { NETWORK_STATE } from "./network-state"

const handleIncrementFinishJobs = (msg) => NETWORK_STATE.finishedJobsNo += msg.data
const handleIncrementFailedJobs = (msg) => NETWORK_STATE.failedJobsNo += msg.data
const handlePlannedJobs = (msg) => NETWORK_STATE.currPlannedJobsNo += msg.data
const handleExecutedJobs = (msg) => NETWORK_STATE.currActiveJobsNo += msg.data
const handleCurrentClientsNumber = (msg) => NETWORK_STATE.currClientsNo += msg.data

export {
    handleIncrementFinishJobs,
    handleIncrementFailedJobs,
    handlePlannedJobs,
    handleExecutedJobs,
    handleCurrentClientsNumber,
}