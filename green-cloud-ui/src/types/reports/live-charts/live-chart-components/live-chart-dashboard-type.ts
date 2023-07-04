import { LiveIndicatorConfiguration } from 'types/reports/live-indicator'
import { LiveChartGenerator } from '../live-chart-generators'

export interface LiveChartDashboardType {
   name: string
   charts: LiveChartGenerator[]
   mainChartId: number
   disableChartDashboard?: boolean
   valueFields?: LiveIndicatorConfiguration[]
}
