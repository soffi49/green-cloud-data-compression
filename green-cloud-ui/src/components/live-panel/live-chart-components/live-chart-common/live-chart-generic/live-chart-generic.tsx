import React from 'react'
import {
   LiveChartDataCategory,
   LiveChartDataCategoryDescription,
   LiveChartEntryNumeric,
   LiveChartAdditionalProps,
   LiveChartEntryTime,
   LiveChartEntry
} from '@types'
import { isTimeWithinBounds } from 'utils/time-utils'

interface Props {
   data: LiveChartDataCategory[]
   chart: React.ElementType<{
      data: LiveChartEntryTime[] | LiveChartEntryNumeric[]
      labels: LiveChartDataCategoryDescription[]
      [key: string]: any
   }>
   timeRestriction?: number
   additionalProps?: LiveChartAdditionalProps
}

/**
 * Component represents a generic live chart
 *
 * @param {LiveChartDataCategory[]}[data] - data displayed in the chart
 * @param {ReactNode}[chart] - chart to be displayed
 * @param {number}[timeRestriction] - optional restriction on time domain
 * @returns JSX Element
 */
export const LiveChartGeneric = ({ data, timeRestriction, chart, additionalProps }: Props) => {
   const Chart = chart

   const formatNumericData = () => data.map((el) => ({ name: el.name, value: Math.round(el.statistics as number) }))

   const formatTimeData = () =>
      (data[0].statistics as LiveChartEntry[])
         .map((entry, idx) => ({
            ...data
               .map((el) => ({ [el.name]: Math.round((el.statistics as LiveChartEntry[])[idx].value) }))
               .reduce((prev, curr) => ({ ...prev, ...curr }), {}),
            time: new Date(entry.time)
         }))
         .filter((entry) => !timeRestriction || isTimeWithinBounds(entry.time, timeRestriction))

   const formatLabeling = () => data.map((entry) => ({ name: entry.name, color: entry.color }))

   const formattedData = typeof data[0].statistics === 'number' ? formatNumericData() : formatTimeData()

   return <Chart data={formattedData} labels={formatLabeling()} {...additionalProps} />
}

export default LiveChartGeneric
