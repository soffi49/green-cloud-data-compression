import { AgentNode } from 'types/agent-nodes'
import { GraphEdge } from 'types/graph'
import { LiveStatisticReport } from 'types/reports'

export type FethGraphStateMessage = {
   nodes: AgentNode[]
   connections: GraphEdge[]
}

export type FetchClientReportsMessage = {
   executedJobsReport: LiveStatisticReport[]
   avgJobSizeReport: LiveStatisticReport[]
   minJobSizeReport: LiveStatisticReport[]
   maxJobSizeReport: LiveStatisticReport[]
}

export type FetchNetworkReportsMessage = {
   finishJobsReport: LiveStatisticReport[]
   failJobsReport: LiveStatisticReport[]
   clientsReport: LiveStatisticReport[]
}

export type FetchManagingReportsMessage = {
   jobSuccessRatioReport: LiveStatisticReport[]
   trafficDistributionReport: LiveStatisticReport[]
   backUpPowerUsageReport: LiveStatisticReport[]
}
