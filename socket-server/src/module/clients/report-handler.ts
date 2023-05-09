import { JOB_STATUSES } from "../../constants/constants"
import { CLIENTS_REPORTS_STATE, CLIENTS_STATE } from "./clients-state"

const reportExecutedJob = (time: number) => {
    const activeStatuses = [JOB_STATUSES.IN_PROGRESS, JOB_STATUSES.ON_BACK_UP, JOB_STATUSES.ON_HOLD]
    const jobsNo = CLIENTS_STATE.clients.filter(client => activeStatuses.includes(client.status)).length

    return ({ time, value: jobsNo })
}

const reportJobSizeData = (time: number) => {
    const activeStatuses = [JOB_STATUSES.IN_PROGRESS, JOB_STATUSES.ON_BACK_UP, JOB_STATUSES.ON_HOLD]
    const jobSizes = CLIENTS_STATE.clients.filter(client => activeStatuses.includes(client.status)).map(client => parseInt(client.job.power))
    const isEmpty = jobSizes.length === 0
    const avg = !isEmpty ? jobSizes.reduce((size1, size2) => size1 + size2, 0) / jobSizes.length : 0

    return ({ time, avg, min: isEmpty ? 0 : Math.min(...jobSizes), max: isEmpty ? 0 : Math.max(...jobSizes) })
}

const updateClientReportsState = (time) => {
    const jobSizeData = reportJobSizeData(time)

    const executedJobsReport = CLIENTS_REPORTS_STATE.executedJobsReport.concat(reportExecutedJob(time))
    const avgJobSizeReport = CLIENTS_REPORTS_STATE.avgJobSizeReport.concat({ time: jobSizeData.time, value: jobSizeData.avg })
    const minJobSizeReport = CLIENTS_REPORTS_STATE.minJobSizeReport.concat({ time: jobSizeData.time, value: jobSizeData.min })
    const maxJobSizeReport = CLIENTS_REPORTS_STATE.maxJobSizeReport.concat({ time: jobSizeData.time, value: jobSizeData.max })

    Object.assign(CLIENTS_REPORTS_STATE, ({ executedJobsReport, avgJobSizeReport, minJobSizeReport, maxJobSizeReport }))
}

export {
    updateClientReportsState
}