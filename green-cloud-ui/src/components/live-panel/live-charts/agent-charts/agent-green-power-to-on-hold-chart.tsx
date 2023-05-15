import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   jobsOnHoldReport: LiveStatisticReport[]
   jobsOnGreenPowerReport: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the number of jobs on hold to the number of jobs executed on green power over time
 *
 * @param {LiveStatisticReport}[jobsOnHoldReport] - report of jobs being on hold
 * @param {LiveStatisticReport}[jobsOnGreenPowerReport] - report of jobs been executed on green power
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const GreenToOnHoldLiveChart = ({ jobsOnHoldReport, jobsOnGreenPowerReport, title }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'jobs on green power no.', color: 'var(--green-1)', statistics: jobsOnGreenPowerReport },
      { name: 'jobs on hold no.', color: 'var(--red-1)', statistics: jobsOnHoldReport },
   ]

   return <LiveChartWrapper {...{ title, chart: LiveLineChart, data: chartData }} />
}

export default GreenToOnHoldLiveChart
