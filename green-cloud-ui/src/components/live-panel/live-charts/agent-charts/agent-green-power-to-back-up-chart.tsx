import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   backUpPowerReport: LiveStatisticReport[]
   greenPowerReport: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the usage of back up power to green power over time
 *
 * @param {LiveStatisticReport}[backUpPowerReport] - report of back up power usage
 * @param {LiveStatisticReport}[greenPowerReport] - report of green power usage
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const GreenToBackUpPowerLiveChart = ({ backUpPowerReport, greenPowerReport, title }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'green power usage', color: 'var(--green-1)', statistics: greenPowerReport },
      { name: 'back-up power usage', color: 'var(--gray-1)', statistics: backUpPowerReport },
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
               yAxisFormatter: formatLabel,
            },
         }}
      />
   )
}

export default GreenToBackUpPowerLiveChart
