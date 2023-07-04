import React from 'react'

import { Line, LineChart, ResponsiveContainer } from 'recharts'
import { LiveChartDataCategoryDescription, LiveChartEntryNumeric, LiveChartEntryTime } from '@types'
import { renderChartDescription } from '../live-chart-common/live-chart-generic/live-chart-generic-config'
import { styles } from '../live-chart-common/live-chart-generic/live-chart-generic-styles'
import { ContentType } from 'recharts/types/component/Tooltip'
import { NameType, ValueType } from 'recharts/types/component/DefaultTooltipContent'

interface Props {
   data: LiveChartEntryTime[] | LiveChartEntryNumeric[]
   labels: LiveChartDataCategoryDescription[]
   valueDomain?: number[]
   yAxisFormatter?: (data: any) => string
   customTooltip?: ContentType<ValueType, NameType>
}

/**
 * Component represents a default live line chart
 *
 * @param {LiveChartEntryTime[]}[data] - data displayed in the chart
 * @param {LiveChartDataCategoryDescription[]}[labeling] - data used in chart labels
 * @param {number[]}[valueDomain] - optional range displayed on y-axis
 * @param {func}[yAxisFormatter] - optional formatter for y-axis labels
 * @param {ContentType}[customTooltip] - optional custom tooltip formatting
 * @returns JSX Element
 */
export const LiveLineChart = ({ data, labels, valueDomain, yAxisFormatter, customTooltip }: Props) => {
   const { chartContainer, chart } = styles

   const getChartLines = () =>
      labels.map((entry) => (
         <Line
            {...{
               key: 'line-' + entry.name,
               type: 'monotone',
               dataKey: entry.name,
               stroke: entry.color,
               strokeWidth: 1.5,
               activeDot: true,
               dot: false,
               isAnimationActive: false
            }}
         />
      ))

   return (
      <ResponsiveContainer {...chartContainer}>
         <LineChart {...chart} data={data}>
            {renderChartDescription({ ...{ valueDomain, yAxisFormatter, customContent: customTooltip } })}
            {getChartLines()}
         </LineChart>
      </ResponsiveContainer>
   )
}

export default LiveLineChart
