import { LiveChartEntry } from '../live-charts/live-chart-entry/live-chart-entry'

export interface CommonAgentReports {
   capacityReport: LiveChartEntry[]
   trafficReport: LiveChartEntry[]
   successRatioReport: LiveChartEntry[]
}
