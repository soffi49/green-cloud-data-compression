import React from 'react'

import { styles } from '../live-chart-common/live-chart-generic/live-chart-generic-styles'

import { Bar, BarChart, ResponsiveContainer } from 'recharts'
import { LiveChartDataCategoryDescription, LiveChartEntryNumeric, LiveChartEntryTime } from '@types'
import { renderChartDescription } from '../live-chart-common/live-chart-generic/live-chart-generic-config'
import { ContentType } from 'recharts/types/component/Tooltip'
import { NameType, ValueType } from 'recharts/types/component/DefaultTooltipContent'

interface Props {
   data: LiveChartEntryTime[] | LiveChartEntryNumeric[]
   labels: LiveChartDataCategoryDescription[]
   valueDomain?: number[]
   yAxisFormatter?: (data: any) => string
   unit?: string
   customTooltip?: ContentType<ValueType, NameType>
}

/**
 * Component represents a default live bar chart
 *
 * @param {LiveChartEntryTime[]}[data] - data displayed in the chart
 * @param {LiveChartDataCategoryDescription[]}[labeling] - data used in chart labels
 * @param {number[]}[valueDomain] - optional range displayed on y-axis
 * @param {func}[yAxisFormatter] - optional formatter for y-axis labels
 * @param {string}[unit] - optional unit displayed next to the bars
 * @returns JSX Element
 */
export const LiveBarChart = ({ data, labels, valueDomain, yAxisFormatter, unit, customTooltip }: Props) => {
   const { chartContainer, chart } = styles
   const getBarData = () =>
      labels.map((label) => ({
         name: label.name,
         fill: label.color,
         value: (data as LiveChartEntryNumeric[]).filter((el) => el.name === label.name)[0].value
      }))

   return (
      <ResponsiveContainer {...chartContainer}>
         <BarChart {...chart} barCategoryGap={1} barSize={100} data={getBarData()}>
            {renderChartDescription({
               ...{
                  isTimeChart: false,
                  valueDomain,
                  yAxisFormatter,
                  customContent: customTooltip,
                  displayAllXAxis: true
               }
            })}
            <Bar {...{ dataKey: 'value', fill: 'var(--green-1)', unit: unit ?? '%' }} />
         </BarChart>
      </ResponsiveContainer>
   )
}

export default LiveBarChart
