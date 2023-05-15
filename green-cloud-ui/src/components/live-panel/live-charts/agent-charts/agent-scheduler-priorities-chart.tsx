import React from 'react'

import { LiveChartWrapper } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'
import LiveBarOverTimeChart from 'components/live-panel/live-chart-components/live-chart-types/live-bar-over-time-chart'

interface Props {
   powerPriorityReport: LiveStatisticReport[]
   deadlinePriorityReport: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the priorities of the scheduler over time
 *
 * @param {LiveStatisticReport}[powerPriorityReport] - report of scheduler power priority
 * @param {LiveStatisticReport}[deadlinePriorityReport] - report of scheduler deadline priority
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const SchedulerPrioritiesLiveChart = ({ powerPriorityReport, deadlinePriorityReport, title }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'power priority', color: 'var(--green-1)', statistics: powerPriorityReport },
      { name: 'deadline priority', color: 'var(--green-2)', statistics: deadlinePriorityReport },
   ]

   return <LiveChartWrapper {...{ title, chart: LiveBarOverTimeChart, data: chartData }} />
}

export default SchedulerPrioritiesLiveChart
