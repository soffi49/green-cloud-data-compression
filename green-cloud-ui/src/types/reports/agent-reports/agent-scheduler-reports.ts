import { LiveChartEntry } from '../live-charts/live-chart-entry/live-chart-entry'

export interface AgentSchedulerStatisticReports {
   powerPriorityReport: LiveChartEntry[]
   deadlinePriorityReport: LiveChartEntry[]
   clientRequestReport: LiveChartEntry[]
   queueCapacityReport: LiveChartEntry[]
   trafficReport: LiveChartEntry[]
}
