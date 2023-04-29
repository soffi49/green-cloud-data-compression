import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   finishJobReport: LiveStatisticReport[]
   failJobReport: LiveStatisticReport[]
}

/**
 * Live chart that displays the number of jobs which execution was successful/failed
 *
 * @param {string}[finishJobReport] - report of finished jobs
 * @param {string}[failJobReport] - report of failed jobs
 * @returns JSX Element
 */
export const JobCompletedLiveChart = ({ finishJobReport, failJobReport }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'finished jobs no.', color: 'var(--green-1)', statistics: finishJobReport },
      { name: 'failed jobs no.', color: 'var(--red-1)', statistics: failJobReport },
   ]

   return (
      <LiveChartWrapper {...{ title: 'Number of completed jobs over time', chart: LiveLineChart, data: chartData }} />
   )
}

export default JobCompletedLiveChart
