import React from 'react'

import { LiveChartWrapper } from '@components'
import { AgentStatisticReport, CommonAgentReports, LiveChartData } from '@types'
import LiveBarChart from 'components/live-panel/live-chart-components/live-chart-types/live-bar-chart'

interface Props {
   agentReports: AgentStatisticReport[]
   title: string
}

/**
 * Live chart that displays the distribution of traffic between selected agents
 *
 * @param {AgentStatisticReport[]}[agentReports] - reports for all agents
 * @param {string}[title] - title of the report
 * @returns JSX Element
 */
export const TrafficDistributionLiveChart = ({ agentReports, title }: Props) => {
   const getChartData = (): LiveChartData[] => {
      const cnaTraffics = agentReports
         .map((agentReports) => (agentReports.reports as CommonAgentReports).trafficReport)
         .map((trafficReport) => (trafficReport.length === 0 ? 0 : trafficReport[trafficReport.length - 1]?.value ?? 0))

      const overallTraffic = cnaTraffics.reduce((sum, val) => sum + val, 0)

      return agentReports.map((agent, idx) => ({
         name: `${agent.name} traffic`,
         color: `var(--green-${idx + 2})`,
         statistics: overallTraffic === 0 ? 0 : (cnaTraffics[idx] / overallTraffic) * 100,
      }))
   }

   const formatLabel = (label: string) => [label, '%'].join('')

   return (
      <LiveChartWrapper
         {...{
            title,
            chart: LiveBarChart,
            data: getChartData(),
            additionalProps: {
               valueDomain: [0, 100],
               yAxisFormatter: formatLabel,
            },
            disableTimeSelector: true,
         }}
      />
   )
}

export default TrafficDistributionLiveChart
