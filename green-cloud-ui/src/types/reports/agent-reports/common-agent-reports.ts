import { LiveStatisticReport } from '../live-statistic-report'

export interface CommonAgentReports {
   capacityReport: LiveStatisticReport[]
   trafficReport: LiveStatisticReport[]
   successRatioReport: LiveStatisticReport[]
}
