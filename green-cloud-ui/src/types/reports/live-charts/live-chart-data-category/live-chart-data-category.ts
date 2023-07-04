import { LiveChartEntry } from '../live-chart-entry/live-chart-entry'

export interface LiveChartDataCategory {
   name: string
   color: string
   statistics: LiveChartEntry[] | number
}
