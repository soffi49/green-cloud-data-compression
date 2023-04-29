import { LiveStatisticReport } from '../live-statistic-report'

export interface AgentSchedulerStatisticReports {
   prioritiesReport: LiveStatisticReport[]
   clientRequestReport: LiveStatisticReport[]
   queueCapacityReport: LiveStatisticReport[]
}
