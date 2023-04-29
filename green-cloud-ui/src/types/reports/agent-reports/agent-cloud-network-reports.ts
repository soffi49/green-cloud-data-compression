import { LiveStatisticReport } from '../live-statistic-report'
import { CommonAgentReports } from './common-agent-reports'

export interface AgentCloudNetworkStatisticReports extends CommonAgentReports {
   clientsReport: LiveStatisticReport[]
}
