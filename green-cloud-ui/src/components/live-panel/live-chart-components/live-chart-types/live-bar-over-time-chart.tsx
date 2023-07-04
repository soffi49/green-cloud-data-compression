import React from 'react'

import { styles } from '../live-chart-common/live-chart-generic/live-chart-generic-styles'

import { Bar, BarChart, ResponsiveContainer } from 'recharts'
import { LiveChartDataCategoryDescription, LiveChartEntryNumeric, LiveChartEntryTime } from '@types'
import { renderChartDescription } from '../live-chart-common/live-chart-generic/live-chart-generic-config'

interface Props {
   data: LiveChartEntryTime[] | LiveChartEntryNumeric[]
   labels: LiveChartDataCategoryDescription[]
   valueDomain?: number[]
   yAxisFormatter?: (data: any) => string
}

/**
 * Component represents a default live bar chart over time
 *
 * @param {LiveChartEntryTime[]}[data] - data displayed in the chart
 * @param {LiveChartDataCategoryDescription[]}[labeling] - data used in chart labels
 * @param {number[]}[valueDomain] - optional range displayed on y-axis
 * @param {func}[yAxisFormatter] - optional formatter for y-axis labels
 * @returns JSX Element
 */
export const LiveBarOverTimeChart = ({ data, labels, valueDomain, yAxisFormatter }: Props) => {
   const { chartContainer, chart } = styles
   const getChartBars = () =>
      labels.map((entry) => (
         <Bar
            {...{
               dataKey: entry.name,
               fill: entry.color
            }}
         />
      ))

   return (
      <ResponsiveContainer {...chartContainer}>
         <BarChart {...chart} barCategoryGap={1} barSize={100} data={data}>
            {renderChartDescription({ ...{ isTimeChart: true, valueDomain, yAxisFormatter } })}
            {getChartBars()}
         </BarChart>
      </ResponsiveContainer>
   )
}

export default LiveBarOverTimeChart
