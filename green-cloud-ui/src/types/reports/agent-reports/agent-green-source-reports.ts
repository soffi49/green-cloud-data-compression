import { CommonAgentReports } from './common-agent-reports'
import { LiveStatisticReport } from '../live-statistic-report'

export interface AgentGreenSourceStatisticReports extends CommonAgentReports {
   availableGreenPowerReport: LiveStatisticReport[]
   jobsOnGreenPowerReport: LiveStatisticReport[]
   jobsOnHoldReport: LiveStatisticReport[]
}
