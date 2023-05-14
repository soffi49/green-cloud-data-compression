import React from 'react'
import {
   LiveChartData,
   LiveChartLabeling,
   LiveChartNumericValues,
   LiveChartProps,
   LiveChartTimeValues,
   LiveStatisticReport,
} from '@types'
import { isTimeWithinBounds } from 'utils/time-utils'

interface Props {
   data: LiveChartData[]
   chart: React.ElementType<{
      data: LiveChartTimeValues[] | LiveChartNumericValues[]
      labels: LiveChartLabeling[]
      [key: string]: any
   }>
   timeRestriction?: number
   additionalProps?: LiveChartProps
}

/**
 * Component represents a generic live chart
 *
 * @param {LiveChartData[]}[data] - data displayed in the chart
 * @param {ReactNode}[chart] - chart to be displayed
 * @param {number}[timeRestriction] - optional restriction on time domain
 * @returns JSX Element
 */
export const LiveChartGeneric = ({ data, timeRestriction, chart, additionalProps }: Props) => {
   const Chart = chart

   const formatNumericData = () => data.map((el) => ({ name: el.name, value: Math.round(el.statistics as number) }))

   const formatTimeData = () =>
      (data[0].statistics as LiveStatisticReport[])
         .map((entry, idx) => ({
            ...data
               .map((el) => ({ [el.name]: Math.round((el.statistics as LiveStatisticReport[])[idx].value) }))
               .reduce((prev, curr) => ({ ...prev, ...curr }), {}),
            time: new Date(entry.time),
         }))
         .filter((entry) => !timeRestriction || isTimeWithinBounds(entry.time, timeRestriction))

   const formatLabeling = () => data.map((entry) => ({ name: entry.name, color: entry.color }))

   const formattedData = typeof data[0].statistics === 'number' ? formatNumericData() : formatTimeData()

   return <Chart data={formattedData} labels={formatLabeling()} {...additionalProps} />
}

export default LiveChartGeneric
