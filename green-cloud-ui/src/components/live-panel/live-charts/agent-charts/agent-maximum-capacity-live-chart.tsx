import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   capacityReport: LiveChartEntry[]
   title: string
}

/**
 * Live chart that displays the amount of maximum capacity of the given agent over time
 *
 * @param {LiveChartEntry[]}[capacityReport] - report of capacity amount
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const AgentMaximumCapacityLiveChart = ({ capacityReport, title }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'maximum capacity', color: 'var(--green-1)', statistics: capacityReport }
   ]

   return <LiveChartWrapper {...{ title, chart: LiveAreaChart, data: chartData }} />
}

export default AgentMaximumCapacityLiveChart
