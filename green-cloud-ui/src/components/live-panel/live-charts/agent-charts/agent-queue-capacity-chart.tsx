import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   queueCapacityReport: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the scheduler's queue capacity over time
 *
 * @param {LiveStatisticReport}[queueCapacityReport] - number of jobs present in the queue
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const QueueCapacityLiveChart = ({ queueCapacityReport, title }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'no. of jobs in the queue', color: 'var(--green-1)', statistics: queueCapacityReport },
   ]

   return <LiveChartWrapper {...{ title, chart: LiveAreaChart, data: chartData }} />
}

export default QueueCapacityLiveChart
