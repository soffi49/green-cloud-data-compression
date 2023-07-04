import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   jobsExecutedAsWhole: LiveChartEntry[]
   jobsExecutedInParts: LiveChartEntry[]
}

/**
 * Live chart that displays the information about jobs executed as a whole and jobs executed in parts over time
 *
 * @param {LiveChartEntry}[jobsExecutedAsWhole] - report of number of jobs executed as whole
 * @param {LiveChartEntry}[jobsExecutedinParts] - report of number of jobs executed in parts
 * @returns JSX Element
 */
export const JobExecutionProportionChart = ({ jobsExecutedAsWhole, jobsExecutedInParts }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'no. of jobs executed as a whole', color: 'var(--green-1)', statistics: jobsExecutedAsWhole },
      { name: 'no. of jobs split into parts', color: 'var(--olive-1)', statistics: jobsExecutedInParts }
   ]

   return <LiveChartWrapper {...{ title: 'Number of job split over time', chart: LiveLineChart, data: chartData }} />
}

export default JobExecutionProportionChart
