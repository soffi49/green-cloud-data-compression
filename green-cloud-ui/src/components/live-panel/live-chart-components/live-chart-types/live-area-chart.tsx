import React from 'react'

import { styles } from '../live-chart-common/live-chart-generic/live-chart-generic-styles'

import { Area, AreaChart, ResponsiveContainer } from 'recharts'
import { LiveChartDataCategoryDescription, LiveChartEntryNumeric, LiveChartEntryTime } from '@types'
import { renderChartDescription } from '../live-chart-common/live-chart-generic/live-chart-generic-config'
import { ContentType } from 'recharts/types/component/Tooltip'
import { NameType, ValueType } from 'recharts/types/component/DefaultTooltipContent'

interface Props {
   data: LiveChartEntryTime[] | LiveChartEntryNumeric[]
   labels: LiveChartDataCategoryDescription[]
   valueDomain?: number[]
   referenceValue?: number
   referenceLabel?: string
   yAxisFormatter?: (data: any) => string
   customTooltip?: ContentType<ValueType, NameType>
}

/**
 * Component represents a default live area chart
 *
 * @param {LiveChartEntryTime[]}[data] - data displayed in the chart
 * @param {LiveChartDataCategoryDescription[]}[labeling] - data used in chart labels
 * @param {number[]}[valueDomain] - optional range displayed on y-axis
 * @param {func}[yAxisFormatter] - optional formatter for y-axis labels
 * @param {ContentType}[customTooltip] - optional custom tooltip formatting
 * @returns JSX Element
 */
export const LiveAreaChart = ({ data, labels, valueDomain, yAxisFormatter, customTooltip }: Props) => {
   const { chartContainer, chart } = styles

   const getChartAreas = () =>
      labels.map((entry) => (
         <Area
            {...{
               key: 'area-' + entry.name,
               type: 'monotone',
               dataKey: entry.name,
               stroke: entry.color,
               fillOpacity: 1,
               fill: 'url(#gradient)',
               activeDot: true,
               dot: false,
               isAnimationActive: false
            }}
         />
      ))

   return (
      <ResponsiveContainer {...chartContainer}>
         <AreaChart {...chart} data={data}>
            <defs>
               <linearGradient id="gradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="var(--green-2)" stopOpacity={0.8} />
                  <stop offset="95%" stopColor="var(--green-3)" stopOpacity={0} />
               </linearGradient>
            </defs>
            {renderChartDescription({ ...{ valueDomain, yAxisFormatter, customContent: customTooltip } })}
            {getChartAreas()}
         </AreaChart>
      </ResponsiveContainer>
   )
}

export default LiveAreaChart
