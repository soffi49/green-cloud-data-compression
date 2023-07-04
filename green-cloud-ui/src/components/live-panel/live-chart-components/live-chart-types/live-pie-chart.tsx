import React from 'react'

import { styles } from '../live-chart-common/live-chart-generic/live-chart-generic-styles'

import { Pie, PieChart, PieLabelRenderProps, ResponsiveContainer } from 'recharts'
import { LiveChartDataCategoryDescription, LiveChartEntryNumeric, LiveChartEntryTime } from '@types'
import { renderChartDescription } from '../live-chart-common/live-chart-generic/live-chart-generic-config'

interface Props {
   data: LiveChartEntryTime[] | LiveChartEntryNumeric[]
   labels: LiveChartDataCategoryDescription[]
   valueDomain?: number[]
   yAxisFormatter?: (data: any) => string
}

/**
 * Component represents a default live pie chart
 *
 * @param {LiveChartEntryTime[]}[data] - data displayed in the chart
 * @param {LiveChartDataCategoryDescription[]}[labeling] - data used in chart labels
 * @param {number[]}[valueDomain] - optional range displayed on y-axis
 * @param {func}[yAxisFormatter] - optional formatter for y-axis labels
 * @returns JSX Element
 */
export const LivePieChart = ({ data, labels, valueDomain, yAxisFormatter }: Props) => {
   const { chartContainer } = styles
   const getPieData = () =>
      labels.map((label) => ({
         name: label.name,
         fill: label.color,
         value: (data as LiveChartEntryNumeric[]).filter((el) => el.name === label.name)[0].value
      }))

   const generateLabel = ({ x, y, textAnchor, percent, fill }: PieLabelRenderProps) => {
      return (
         <text {...{ x, y, textAnchor, fontWeight: '500', fontSize: 'var(--font-size-11)', fill }}>
            {`${((percent ?? 0) * 100).toFixed(0)}%`}
         </text>
      )
   }

   return (
      <ResponsiveContainer {...chartContainer}>
         <PieChart {...{ margin: { top: 20, right: 5, bottom: 5, left: 5 } }}>
            {renderChartDescription({ ...{ isTimeChart: false, valueDomain, yAxisFormatter, hasAxises: false } })}
            <Pie
               {...{
                  data: getPieData(),
                  dataKey: 'value',
                  nameKey: 'name',
                  labelLine: true,
                  label: generateLabel
               }}
            />
         </PieChart>
      </ResponsiveContainer>
   )
}

export default LivePieChart
