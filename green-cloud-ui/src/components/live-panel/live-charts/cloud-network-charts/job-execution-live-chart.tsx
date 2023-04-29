import React from 'react'

import { LiveAreaChart, LiveChartWrapper } from '@components'
import { LiveChartData, LiveChartTooltip, LiveStatisticReport } from '@types'
import { ContentType } from 'recharts/types/component/Tooltip'
import { NameType, ValueType } from 'recharts/types/component/DefaultTooltipContent'
import { renderCustomTooltipContent } from 'components/live-panel/live-chart-components/live-chart-common/live-chart-generic/live-chart-generic-config'

interface Props {
   executedJobsReport: LiveStatisticReport[]
   avgJobSizeReport: LiveStatisticReport[]
   minJobSizeReport: LiveStatisticReport[]
   maxJobSizeReport: LiveStatisticReport[]
}

/**
 * Live chart that displays the number of executed jobs at particular time
 *
 * @param {string}[executedJobsReport] - report of currently executed jobs
 * @param {string}[avgJobSizeReport] - report of average job size
 * @param {string}[minJobSizeReport] - report of minimum job size
 *  @param {string}[maxJobSizeReport] - report of maximum job size
 * @returns JSX Element
 */
export const JobExecutionLiveChart = ({
   executedJobsReport,
   avgJobSizeReport,
   minJobSizeReport,
   maxJobSizeReport,
}: Props) => {
   const chartData: LiveChartData[] = [
      { name: 'executed jobs no.', color: 'var(--green-1)', statistics: executedJobsReport },
   ]

   const CustomTooltip: ContentType<ValueType, NameType> = ({ active, payload, label }) => {
      if (active && payload && payload.length) {
         const time = (label as Date).getTime()
         const data: LiveChartTooltip[] = [
            { name: 'avg. job size', value: avgJobSizeReport.filter((data) => data.time === time)[0]?.value },
            { name: 'min. job size', value: minJobSizeReport.filter((data) => data.time === time)[0]?.value },
            { name: 'max. job size', value: maxJobSizeReport.filter((data) => data.time === time)[0]?.value },
         ]
         return renderCustomTooltipContent(label, payload, data)
      }
   }

   return (
      <LiveChartWrapper
         {...{
            title: 'Number of executed jobs over time',
            chart: LiveAreaChart,
            data: chartData,
            additionalProps: { customTooltip: CustomTooltip },
         }}
      />
   )
}

export default JobExecutionLiveChart
