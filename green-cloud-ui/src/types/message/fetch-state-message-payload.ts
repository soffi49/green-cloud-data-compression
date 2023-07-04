import { AgentNode } from 'types/agent-nodes'
import { GraphEdge } from 'types/graph'
import { LiveChartEntry } from 'types/reports'

export type FethGraphStateMessage = {
   nodes: AgentNode[]
   connections: GraphEdge[]
}

export type FetchClientReportsMessage = {
   executedJobsReport: LiveChartEntry[]
   avgJobSizeReport: LiveChartEntry[]
   minJobSizeReport: LiveChartEntry[]
   maxJobSizeReport: LiveChartEntry[]
}

export type FetchNetworkReportsMessage = {
   finishJobsReport: LiveChartEntry[]
   failJobsReport: LiveChartEntry[]
   clientsReport: LiveChartEntry[]
}

export type FetchManagingReportsMessage = {
   jobSuccessRatioReport: LiveChartEntry[]
   trafficDistributionReport: LiveChartEntry[]
   backUpPowerUsageReport: LiveChartEntry[]
}
