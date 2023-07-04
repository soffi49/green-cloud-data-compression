import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartDataCategory, LiveChartEntry } from '@types'

interface Props {
   availableGreenPower: LiveChartEntry[]
   title: string
}

/**
 * Live chart that displays the available green power over time
 *
 * @param {LiveChartEntry}[availableGreenPower] - amount of available green power over time
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const AvailableGreenPowerLiveChart = ({ availableGreenPower, title }: Props) => {
   const chartData: LiveChartDataCategory[] = [
      { name: 'available green power', color: 'var(--green-1)', statistics: availableGreenPower }
   ]

   return <LiveChartWrapper {...{ title, chart: LiveAreaChart, data: chartData }} />
}

export default AvailableGreenPowerLiveChart
