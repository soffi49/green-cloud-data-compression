import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   trafficReport: LiveChartEntry[]
   title: string
}

/**
 * Live chart that displays how the traffic was changing over time
 *
 * @param {LiveChartEntry[]}[trafficReport] - report of traffic
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const TrafficLiveChart = ({ trafficReport, title }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'traffic %', color: 'var(--green-1)', statistics: trafficReport }
   ]
   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title,
            chart: LiveAreaChart,
            data: chartData,
            additionalProps: {
               valueDomain: [0, 100],
               yAxisFormatter: formatLabel
            }
         }}
      />
   )
}

export default TrafficLiveChart
