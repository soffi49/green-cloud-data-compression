import { LiveStatisticReport } from './live-statistic-report'

export interface LiveChartData {
   name: string
   color: string
   statistics: LiveStatisticReport[] | number
}
