import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartDataCategory, LiveChartEntry, LiveChartTooltip } from '@types'
import { ContentType } from 'recharts/types/component/Tooltip'
import { NameType, ValueType } from 'recharts/types/component/DefaultTooltipContent'
import { renderCustomTooltipContent } from 'components/live-panel/live-chart-components/live-chart-common/live-chart-generic/live-chart-generic-config'

interface Props {
   avgClientsExecutionPercentage: LiveChartEntry[]
   minClientsExecutionPercentage: LiveChartEntry[]
   maxClientsExecutionPercentage: LiveChartEntry[]
}

/**
 * Live chart that displays the information about average job execution percentage over time
 *
 * @param {LiveChartEntry}[avgClientsExecutionPercentage] - report of average jobs execution percentage
 * @param {LiveChartEntry}[minClientsExecutionPercentage] - report of minimum jobs execution percentage
 * @param {LiveChartEntry}[maxClientsExecutionPercentage] - report of maximum jobs execution percentage
 * @returns JSX Element
 */
export const JobExecutionPercentageChart = ({
   avgClientsExecutionPercentage,
   minClientsExecutionPercentage,
   maxClientsExecutionPercentage
}: Props) => {
   const chartData: LiveChartDataCategory[] = [
      {
         name: 'avg. job execution %',
         color: 'var(--green-1)',
         statistics: avgClientsExecutionPercentage
      }
   ]

   const CustomTooltip: ContentType<ValueType, NameType> = ({ active, payload, label }) => {
      if (active && payload && payload.length) {
         const time = (label as Date).getTime()
         const data: LiveChartTooltip[] = [
            {
               name: 'avg. job execution %',
               value: avgClientsExecutionPercentage.filter((data) => data.time === time)[0]?.value
            },
            {
               name: 'min. job execution %',
               value: minClientsExecutionPercentage.filter((data) => data.time === time)[0]?.value
            },
            {
               name: 'max. job percentage %',
               value: maxClientsExecutionPercentage.filter((data) => data.time === time)[0]?.value
            }
         ]
         return renderCustomTooltipContent(label, payload, data)
      }
   }

   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title: 'Avg. job execution percentage over time',
            chart: LiveAreaChart,
            data: chartData,
            additionalProps: { customTooltip: CustomTooltip, valueDomain: [0, 100], yAxisFormatter: formatLabel }
         }}
      />
   )
}

export default JobExecutionPercentageChart
