import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   maxJobSizeReport: LiveChartEntry[]
   minJobSizeReport: LiveChartEntry[]
   avgJobSizeReport: LiveChartEntry[]
}

/**
 * Live chart that displays the average, minimal and maximal job execution size over time
 *
 * @param {LiveChartEntry}[maxJobSizeReport] - report of max job size
 * @param {LiveChartEntry}[minJobSizeReport] - report of min job size
 * @param {LiveChartEntry}[avgJobSizeReport] - report of avergae job size
 * @returns JSX Element
 */
export const JobExecutionSizeLiveChart = ({ maxJobSizeReport, minJobSizeReport, avgJobSizeReport }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'max. job size', color: 'var(--red-1)', statistics: maxJobSizeReport },
      { name: 'min. job size', color: 'var(--green-1)', statistics: minJobSizeReport },
      { name: 'avg. job size', color: 'var(--orange-1)', statistics: avgJobSizeReport }
   ]

   return <LiveChartWrapper {...{ title: 'Job size statistics over time', chart: LiveLineChart, data: chartData }} />
}

export default JobExecutionSizeLiveChart
