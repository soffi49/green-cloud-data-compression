import React from 'react'

import { LiveChartWrapper } from '@components'
import { AdaptationGoal } from '@types'
import LivePieChart from 'components/live-panel/live-chart-components/live-chart-types/live-pie-chart'
import { getColorByName } from 'components/live-panel/config/live-panel-config'

interface Props {
   goals: AdaptationGoal[]
}

/**
 * Live chart that displays the contirbution of goals in overall system quality
 *
 * @param {AdaptationGoal[]}[goals] - adaptation goals taken into account in the system
 * @returns JSX Element
 */
export const GoalContributionLiveChart = ({ goals }: Props) => {
   const generatePieData = () =>
      goals.map((goal) => ({
         name: goal.name,
         color: getColorByName(goal.name),
         statistics: goal.weight * 100
      }))

   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title: 'Contribution of goals in system',
            chart: LivePieChart,
            data: generatePieData(),
            additionalProps: {
               valueDomain: [0, 100],
               yAxisFormatter: formatLabel
            },
            disableTimeSelector: true
         }}
      />
   )
}

export default GoalContributionLiveChart
