import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   capacityReport: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the amount of maximum capacity of the given agent over time
 *
 * @param {LiveStatisticReport[]}[capacityReport] - report of capacity amount
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const AgentMaximumCapacityLiveChart = ({ capacityReport, title }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'maximum capacity', color: 'var(--green-1)', statistics: capacityReport },
   ]

   return <LiveChartWrapper {...{ title, chart: LiveAreaChart, data: chartData }} />
}

export default AgentMaximumCapacityLiveChart
