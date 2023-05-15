import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   maxJobSizeReport: LiveStatisticReport[]
   minJobSizeReport: LiveStatisticReport[]
   avgJobSizeReport: LiveStatisticReport[]
}

/**
 * Live chart that displays the average, minimal and maximal job execution size over time
 *
 * @param {LiveStatisticReport}[maxJobSizeReport] - report of max job size
 * @param {LiveStatisticReport}[minJobSizeReport] - report of min job size
 * @param {LiveStatisticReport}[avgJobSizeReport] - report of avergae job size
 * @returns JSX Element
 */
export const JobExecutionSizeLiveChart = ({ maxJobSizeReport, minJobSizeReport, avgJobSizeReport }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'max. job size', color: 'var(--red-1)', statistics: maxJobSizeReport },
      { name: 'min. job size', color: 'var(--green-1)', statistics: minJobSizeReport },
      { name: 'avg. job size', color: 'var(--orange-1)', statistics: avgJobSizeReport },
   ]

   return <LiveChartWrapper {...{ title: 'Job size statistics over time', chart: LiveLineChart, data: chartData }} />
}

export default JobExecutionSizeLiveChart
