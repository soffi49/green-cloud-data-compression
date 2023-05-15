import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   jobSuccessRatioReport: LiveStatisticReport[]
   trafficDistributionReport: LiveStatisticReport[]
   backUpPowerUsageReport: LiveStatisticReport[]
}

/**
 * Live chart that displays the values of quality properties over time
 *
 * @param {LiveStatisticReport}[jobSuccessRatioReport] - report of job success ratio
 * @param {LiveStatisticReport}[trafficDistributionReport] - report of traffic distribution
 * @param {LiveStatisticReport}[backUpPowerUsageReport] - report of back-up power usage
 * @returns JSX Element
 */
export const QualityPropertiesLiveChart = ({
   jobSuccessRatioReport,
   trafficDistributionReport,
   backUpPowerUsageReport,
}: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'job success ratio quality (%)', color: 'var(--green-4)', statistics: jobSuccessRatioReport },
      { name: 'traffic distribution quality (%)', color: 'var(--green-1)', statistics: trafficDistributionReport },
      { name: 'back-up power usage quality (%)', color: 'var(--olive-1)', statistics: backUpPowerUsageReport },
   ]
   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title: 'System qualities over time',
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

export default QualityPropertiesLiveChart
