import { LiveChartEntry } from '../live-charts/live-chart-entry/live-chart-entry'
import { CommonAgentReports } from './common-agent-reports'

export interface AgentCloudNetworkStatisticReports extends CommonAgentReports {
   clientsReport: LiveChartEntry[]
}
