import { AgentStatisticReport, LiveChartEntry } from 'types/reports'
import { ClientsStatusReport } from 'types/reports/client-reports'

export type ReportsStore = {
   systemStartTime: number | null
   secondsPerHour: number | null
   finishJobsReport: LiveChartEntry[]
   failJobsReport: LiveChartEntry[]
   systemTrafficReport: LiveChartEntry[]
   executedJobsReport: LiveChartEntry[]
   clientsReport: LiveChartEntry[]
   avgJobSizeReport: LiveChartEntry[]
   minJobSizeReport: LiveChartEntry[]
   maxJobSizeReport: LiveChartEntry[]
   jobSuccessRatioReport: LiveChartEntry[]
   trafficDistributionReport: LiveChartEntry[]
   backUpPowerUsageReport: LiveChartEntry[]
   agentsReports: AgentStatisticReport[]
   clientsStatusReport: ClientsStatusReport[]
   jobsExecutedAsWhole: LiveChartEntry[]
   jobsExecutedInParts: LiveChartEntry[]
   avgClientsExecutionPercentage: LiveChartEntry[]
   maxClientsExecutionPercentage: LiveChartEntry[]
   minClientsExecutionPercentage: LiveChartEntry[]
}
