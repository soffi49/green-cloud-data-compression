import React from 'react'

import { LiveChartWrapper, LiveLineChart } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   clientsReport: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the number of clients over time
 *
 * @param {LiveStatisticReport[]}[clientsReport] - report of clients number
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const SystemClientLiveChart = ({ clientsReport, title }: Props) => {
   const chartData: LiveChartData[] = [{ name: 'clients no.', color: 'var(--green-1)', statistics: clientsReport }]

   return <LiveChartWrapper {...{ title, chart: LiveLineChart, data: chartData }} />
}

export default SystemClientLiveChart
