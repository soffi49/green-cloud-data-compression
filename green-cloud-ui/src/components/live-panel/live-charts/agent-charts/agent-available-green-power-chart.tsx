import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartData, LiveStatisticReport } from '@types'

interface Props {
   availableGreenPower: LiveStatisticReport[]
   title: string
}

/**
 * Live chart that displays the available green power over time
 *
 * @param {LiveStatisticReport}[availableGreenPower] - amount of available green power over time
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const AvailableGreenPowerLiveChart = ({ availableGreenPower, title }: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'available green power', color: 'var(--green-1)', statistics: availableGreenPower },
   ]

   return <LiveChartWrapper {...{ title, chart: LiveAreaChart, data: chartData }} />
}

export default AvailableGreenPowerLiveChart
