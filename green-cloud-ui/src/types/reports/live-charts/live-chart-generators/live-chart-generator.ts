import { AgentStatisticReport } from 'types/reports/agent-reports'
import { ReportsStore } from 'types/store'

export type LiveChartGenerator = (reports?: ReportsStore, agentReports?: AgentStatisticReport | null) => React.ReactNode
