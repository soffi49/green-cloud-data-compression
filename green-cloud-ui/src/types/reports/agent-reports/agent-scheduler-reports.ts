import { LiveStatisticReport } from '../live-statistic-report'

export interface AgentSchedulerStatisticReports {
   powerPriorityReport: LiveStatisticReport[]
   deadlinePriorityReport: LiveStatisticReport[]
   clientRequestReport: LiveStatisticReport[]
   queueCapacityReport: LiveStatisticReport[]
   trafficReport: LiveStatisticReport[]
}
