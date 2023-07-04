import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   finishJobsReport: LiveChartEntry[]
   failJobsReport: LiveChartEntry[]
}

/**
 * Live chart that displays the number of jobs which execution was successful/failed
 *
 * @param {string}[finishJobsReport] - report of finished jobs
 * @param {string}[failJobsReport] - report of failed jobs
 * @returns JSX Element
 */
export const JobCompletedLiveChart = ({ finishJobsReport, failJobsReport }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'finished jobs no.', color: 'var(--green-1)', statistics: finishJobsReport },
      { name: 'failed jobs no.', color: 'var(--red-1)', statistics: failJobsReport }
   ]

   return (
      <LiveChartWrapper {...{ title: 'Number of completed jobs over time', chart: LiveLineChart, data: chartData }} />
   )
}

export default JobCompletedLiveChart
