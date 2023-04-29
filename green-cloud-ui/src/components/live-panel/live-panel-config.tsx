import {
   AgentCloudNetworkStatisticReports,
   AgentStatisticReport,
   AgentType,
   CloudNetworkAgent,
   CommonAgentReports,
   IconElement,
   ReportsStore,
   ServerAgent,
} from '@types'
import { PercentageIndicator, SelectOption, ValueIndicator } from 'components/common'
import { getAverage } from '@utils'
import { IconClients, IconGear } from '@assets'
import {
   AgentMaximumCapacityLiveChart,
   JobCompletedLiveChart,
   JobExecutionLiveChart,
   ClientsNumberLiveChart,
   TrafficDistributionLiveChart,
   TrafficLiveChart,
} from './live-charts'
import { useSelector } from 'react-redux'
import { RootState, selectChosenNetworkAgent } from '@store'

type ChartGenerator = (reports?: ReportsStore, agentReports?: AgentStatisticReport | null) => React.ReactNode

interface ChartOptions {
   systemJobFinishSuccess: ChartGenerator
   systemTraffic: ChartGenerator
   systemClients: ChartGenerator
   systemJobExecution: ChartGenerator
   systemTrafficDistribution: ChartGenerator
   agentClients: ChartGenerator
   agentTraffic: ChartGenerator
   agentTrafficDistribution: ChartGenerator
   agentMaximumCapacity: ChartGenerator
}

type AvgGenerator = (reports: ReportsStore | AgentStatisticReport) => number

interface AvgConfiguration {
   title: string
   value: AvgGenerator
   icon?: IconElement
   indicator: React.ElementType<{
      title: string
      value: number
      icon?: IconElement
   }>
}

interface AvgOptions {
   systemAvgTraffic: AvgConfiguration
   systemAvgClients: AvgConfiguration
   systemAvgJobs: AvgConfiguration
}

interface LiveDashboard {
   [key: string]: {
      name: string
      charts: ChartGenerator[]
      valueFields?: AvgConfiguration[]
   }
}

interface TimeOptions {
   [key: string]: SelectOption
}

export const CHARTS: ChartOptions = {
   systemJobFinishSuccess: (reports) => {
      const reportsStore = reports as ReportsStore
      return (
         <JobCompletedLiveChart
            {...{ failJobReport: reportsStore.failJobsReport, finishJobReport: reportsStore.finishJobsReport }}
         />
      )
   },
   systemTraffic: (reports) => {
      const reportsStore = reports as ReportsStore
      return (
         <TrafficLiveChart {...{ title: 'Total traffic over time', trafficReport: reportsStore.systemTrafficReport }} />
      )
   },
   systemClients: (reports) => {
      const reportsStore = reports as ReportsStore
      return (
         <ClientsNumberLiveChart
            {...{ title: 'Number of clients over time', clientsReport: reportsStore.clientsReport }}
         />
      )
   },
   systemJobExecution: (reports) => {
      const reportsStore = reports as ReportsStore
      return (
         <JobExecutionLiveChart
            {...{
               executedJobsReport: reportsStore.executedJobsReport,
               avgJobSizeReport: reportsStore.avgJobSizeReport,
               minJobSizeReport: reportsStore.minJobSizeReport,
               maxJobSizeReport: reportsStore.maxJobSizeReport,
            }}
         />
      )
   },
   systemTrafficDistribution: (reports) => {
      const reportsStore = reports as ReportsStore
      return (
         <TrafficDistributionLiveChart
            {...{
               title: 'Traffic distribution per CNA',
               agentReports: reportsStore.agentsReports.filter((report) => report.type === AgentType.CLOUD_NETWORK),
            }}
         />
      )
   },
   agentClients: (_, agentReports) => {
      const reportsMapped = agentReports as AgentStatisticReport
      const cnaReports = reportsMapped.reports as AgentCloudNetworkStatisticReports
      return (
         <ClientsNumberLiveChart
            {...{ title: `Number of ${reportsMapped.name} clients over time`, clientsReport: cnaReports.clientsReport }}
         />
      )
   },
   agentTraffic: (_, agentReports) => {
      const reportsMapped = agentReports as AgentStatisticReport
      const trafficReport = (reportsMapped.reports as CommonAgentReports).trafficReport
      return <TrafficLiveChart {...{ title: `${reportsMapped.name} traffic over time`, trafficReport }} />
   },
   agentTrafficDistribution: (reports) => {
      const selectedAgent = useSelector((state: RootState) => selectChosenNetworkAgent(state))
      const connectedAgents =
         selectedAgent?.type === AgentType.CLOUD_NETWORK
            ? { type: 'Servers', agents: (selectedAgent as CloudNetworkAgent).serverAgents }
            : { type: 'Green Sources', agents: (selectedAgent as ServerAgent).greenEnergyAgents }

      const reportsStore = reports as ReportsStore
      const agentReports = reportsStore.agentsReports.filter((report) => connectedAgents.agents.includes(report.name))
      return (
         <TrafficDistributionLiveChart
            {...{
               title: `${selectedAgent?.name} traffic distribution per ${connectedAgents.type}`,
               agentReports: agentReports,
            }}
         />
      )
   },
   agentMaximumCapacity: (_, agentReports) => {
      const reportsMapped = agentReports as AgentStatisticReport
      const capacityReport = (reportsMapped.reports as CommonAgentReports).capacityReport
      return (
         <AgentMaximumCapacityLiveChart
            {...{ title: `Maximum capacity of ${reportsMapped.name} over time`, capacityReport }}
         />
      )
   },
}

const AVG_INDICATORS: AvgOptions = {
   systemAvgTraffic: {
      title: 'Average traffic',
      value: (reports) => Math.round(getAverage((reports as ReportsStore).systemTrafficReport, 'value')),
      indicator: PercentageIndicator,
   },
   systemAvgClients: {
      title: 'Average client number',
      value: (reports) => Math.round(getAverage((reports as ReportsStore).clientsReport, 'value')),
      icon: IconClients,
      indicator: ValueIndicator,
   },
   systemAvgJobs: {
      title: 'Average jobs number',
      value: (reports) => Math.round(getAverage((reports as ReportsStore).executedJobsReport, 'value')),
      icon: IconGear,
      indicator: ValueIndicator,
   },
}

export const CHART_MODALS: LiveDashboard = {
   cloud: {
      name: 'System statistics reports',
      charts: [
         CHARTS.systemClients,
         CHARTS.systemJobExecution,
         CHARTS.systemJobFinishSuccess,
         CHARTS.systemTraffic,
         CHARTS.systemTrafficDistribution,
      ],
      valueFields: [AVG_INDICATORS.systemAvgTraffic, AVG_INDICATORS.systemAvgClients, AVG_INDICATORS.systemAvgJobs],
   },
   [`agent${AgentType.CLOUD_NETWORK}`]: {
      name: 'Cloud Network Agent reports',
      charts: [CHARTS.agentClients, CHARTS.agentMaximumCapacity, CHARTS.agentTraffic, CHARTS.agentTrafficDistribution],
      valueFields: [],
   },
}

export const TIME_OPTIONS: TimeOptions = {
   DAY: { label: 'DAY', value: 60 * 24, isSelected: true },
   WEEK: { label: 'WEEK', value: 60 * 24 * 7, isSelected: false },
   MONTH: { label: '30 DAYS', value: 60 * 24 * 7 * 30, isSelected: false },
}
