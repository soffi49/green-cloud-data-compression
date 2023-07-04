import { AgentStatisticReport } from 'types/reports/agent-reports'
import { ReportsStore } from 'types/store'

export type LiveIndicatorAvgGenerator = (reports: ReportsStore | AgentStatisticReport) => number | string
