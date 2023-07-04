import { LiveLineChart, LiveChartWrapper } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   successRatio: LiveChartEntry[]
   title: string
}

/**
 * Live chart that displays the success ratio over time
 *
 * @param {LiveChartEntry[]}[clientsReport] - report of success ratio
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const SuccessRatioLiveChart = ({ successRatio, title }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'success ratio (%)', color: 'var(--green-1)', statistics: successRatio }
   ]
   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title,
            chart: LiveLineChart,
            data: chartData,
            additionalProps: {
               valueDomain: [0, 100],
               yAxisFormatter: formatLabel
            }
         }}
      />
   )
}

export default SuccessRatioLiveChart
