import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   clientsReport: LiveChartEntry[]
   title: string
}

/**
 * Live chart that displays the number of clients over time
 *
 * @param {LiveChartEntry[]}[clientsReport] - report of clients number
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const SystemClientLiveChart = ({ clientsReport, title }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'clients no.', color: 'var(--green-1)', statistics: clientsReport }
   ]

   return <LiveChartWrapper {...{ title, chart: LiveLineChart, data: chartData }} />
}

export default SystemClientLiveChart
