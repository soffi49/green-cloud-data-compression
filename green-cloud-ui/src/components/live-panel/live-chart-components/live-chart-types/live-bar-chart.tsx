import React from 'react'

import { styles } from '../live-chart-common/live-chart-generic/live-chart-generic-styles'

import { Bar, BarChart, ResponsiveContainer } from 'recharts'
import { LiveChartLabeling, LiveChartNumericValues, LiveChartTimeValues } from '@types'
import { renderChartDescription } from '../live-chart-common/live-chart-generic/live-chart-generic-config'

interface Props {
   data: LiveChartTimeValues[] | LiveChartNumericValues[]
   labels: LiveChartLabeling[]
   valueDomain?: number[]
   yAxisFormatter?: (data: any) => string
}

/**
 * Component represents a default live area chart
 *
 * @param {LiveChartTimeValues[]}[data] - data displayed in the chart
 * @param {LiveChartLabeling[]}[labeling] - data used in chart labels
 * @param {number[]}[valueDomain] - optional range displayed on y-axis
 * @param {func}[yAxisFormatter] - optional formatter for y-axis labels
 * @returns JSX Element
 */
export const LiveBarChart = ({ data, labels, valueDomain, yAxisFormatter }: Props) => {
   const { chartContainer, chart } = styles
   const getBarData = () =>
      labels.map((label) => ({
         name: label.name,
         fill: label.color,
         value: (data as LiveChartNumericValues[]).filter((el) => el.name === label.name)[0].value,
      }))

   return (
      <ResponsiveContainer {...chartContainer}>
         <BarChart {...chart} barCategoryGap={1} barSize={100} data={getBarData()}>
            {renderChartDescription({ ...{ isTimeChart: false, valueDomain, yAxisFormatter } })}
            <Bar {...{ dataKey: 'value', fill: 'var(--green-1)', unit: '%' }} />
         </BarChart>
      </ResponsiveContainer>
   )
}

export default LiveBarChart
