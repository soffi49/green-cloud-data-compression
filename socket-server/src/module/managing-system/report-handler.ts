import { MANAGING_SYSTEM_REPORTS, MANAGING_SYSTEM_STATE } from "./managing-system-state"

const reportJobSuccessRatio = (time: number) => ({ time, value: Math.round(MANAGING_SYSTEM_STATE.goalQualityIndicators[0].quality * 100) })
const reportTrafficDistribution = (time: number) => ({ time, value: Math.round(MANAGING_SYSTEM_STATE.goalQualityIndicators[1].quality * 100) })
const reportBackUpPowerUsage = (time: number) => ({ time, value: Math.round(MANAGING_SYSTEM_STATE.goalQualityIndicators[2].quality * 100) })

const updateManagingSystemReportsState = (time) => {
    if (MANAGING_SYSTEM_STATE.goalQualityIndicators.length >= 3) {
        const backUpPowerUsageReport = MANAGING_SYSTEM_REPORTS.backUpPowerUsageReport.concat(reportBackUpPowerUsage(time))
        const jobSuccessRatioReport = MANAGING_SYSTEM_REPORTS.jobSuccessRatioReport.concat(reportJobSuccessRatio(time))
        const trafficDistributionReport = MANAGING_SYSTEM_REPORTS.trafficDistributionReport.concat(reportTrafficDistribution(time))

        Object.assign(MANAGING_SYSTEM_REPORTS, ({ backUpPowerUsageReport, jobSuccessRatioReport, trafficDistributionReport }))
    }
}

export {
    updateManagingSystemReportsState
}