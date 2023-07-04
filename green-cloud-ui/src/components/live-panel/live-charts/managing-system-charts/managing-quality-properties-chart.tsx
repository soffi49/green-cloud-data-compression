import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'
import { GOALS_COLORS } from 'components/live-panel/config/live-panel-config'

interface Props {
   jobSuccessRatioReport: LiveChartEntry[]
   trafficDistributionReport: LiveChartEntry[]
   backUpPowerUsageReport: LiveChartEntry[]
}

/**
 * Live chart that displays the values of quality properties
 *
 * @param {LiveChartEntry}[jobSuccessRatioReport] - report of job success ratio
 * @param {LiveChartEntry}[trafficDistributionReport] - report of traffic distribution
 * @param {LiveChartEntry}[backUpPowerUsageReport] - report of back-up power usage
 * @returns JSX Element
 */
export const QualityPropertiesLiveChart = ({
   jobSuccessRatioReport,
   trafficDistributionReport,
   backUpPowerUsageReport
}: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'job success ratio quality (%)', color: GOALS_COLORS.SUCCESS_RATIO, statistics: jobSuccessRatioReport },
      {
         name: 'traffic distribution quality (%)',
         color: GOALS_COLORS.TRAFFIC_DISTRIBUTION,
         statistics: trafficDistributionReport
      },
      { name: 'back-up power usage quality (%)', color: GOALS_COLORS.BACK_UP_POWER, statistics: backUpPowerUsageReport }
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
               yAxisFormatter: formatLabel
            }
         }}
      />
   )
}

export default QualityPropertiesLiveChart
