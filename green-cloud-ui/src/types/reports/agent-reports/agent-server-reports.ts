import { LiveStatisticReport } from '../live-statistic-report'
import { CommonAgentReports } from './common-agent-reports'

export interface AgentServerStatisticReports extends CommonAgentReports {
   greenPowerUsageReport: LiveStatisticReport[]
   backUpPowerUsageReport: LiveStatisticReport[]
}
