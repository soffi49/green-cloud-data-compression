import { LiveLineChart, LiveChartWrapper } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   successRatio: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the success ratio over time
 *
 * @param {LiveStatisticReport[]}[clientsReport] - report of success ratio
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const SuccessRatioLiveChart = ({ successRatio, title }: Props) => {
   const chartData: LiveChartData[] = [{ name: 'success ratio (%)', color: 'var(--green-1)', statistics: successRatio }]
   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title,
            chart: LiveLineChart,
            data: chartData,
            additionalProps: {
               valueDomain: [0, 100],
               yAxisFormatter: formatLabel,
            },
         }}
      />
   )
}

export default SuccessRatioLiveChart
