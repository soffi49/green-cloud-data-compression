import { AgentStatisticReport, LiveStatisticReport } from 'types/reports'

export type ReportsStore = {
   systemStartTime: number | null
   secondsPerHour: number | null
   finishJobsReport: LiveStatisticReport[]
   failJobsReport: LiveStatisticReport[]
   systemTrafficReport: LiveStatisticReport[]
   executedJobsReport: LiveStatisticReport[]
   clientsReport: LiveStatisticReport[]
   avgJobSizeReport: LiveStatisticReport[]
   minJobSizeReport: LiveStatisticReport[]
   maxJobSizeReport: LiveStatisticReport[]
   agentsReports: AgentStatisticReport[]
}
