import React, { useState } from 'react'

import { styles } from './live-panel-styles'

import { Button, Card, SubtitleContainer } from '@components'
import { Agent, AgentStatisticReport, MenuTab, ReportsStore } from '@types'
import { CHART_MODALS } from './live-panel-config'
import LiveChartModal from './live-chart-modal/live-chart-modal'

interface Props {
   selectedTab: MenuTab
   reports: ReportsStore
   agentReports: AgentStatisticReport | null
   agentData: Agent | null
   selectedAgent: string | null
}

const headerTitle = 'Live statistics'
const buttonTitle = 'Display all statistics'

/**
 * Component represents a panel gathering all infromations about cloud network
 *
 * @returns JSX Element
 */
export const LivePanel = ({ selectedTab, reports, agentReports, agentData, selectedAgent }: Props) => {
   const [isOpen, setIsOpen] = useState<boolean>(false)
   const { mainContainer, contentContainer, headerContainer, headerStyle } = styles

   const isAgentTab = selectedTab === MenuTab.AGENTS
   const hasAgentData = agentData !== null && selectedAgent === agentData.name
   const hasData = (!isAgentTab && reports.clientsReport.length !== 0) || (isAgentTab && agentReports && hasAgentData)
   const reportsId = isAgentTab && agentReports ? `agent${agentReports?.type}` : selectedTab.toString()
   const chartsData = CHART_MODALS[reportsId]
   const isChartDashboardEnabled = (hasData && chartsData && !chartsData.disableChartDashboard) ?? false

   const buttonClassName = [
      'medium-green-button',
      isChartDashboardEnabled ? 'medium-green-button-active' : 'medium-green-button-disabled',
   ].join(' ')

   const header = (
      <div style={headerContainer}>
         <div style={headerStyle}>{headerTitle.toUpperCase()}</div>
         <Button
            {...{
               title: buttonTitle.toUpperCase(),
               onClick: () => setIsOpen(!isOpen),
               isDisabled: !isChartDashboardEnabled,
               buttonClassName,
            }}
         />
      </div>
   )

   return (
      <Card {...{ containerStyle: mainContainer, contentStyle: contentContainer, header, removeScroll: true }}>
         {!hasData || chartsData === undefined ? (
            <SubtitleContainer text="No data available yet" />
         ) : (
            <>
               <LiveChartModal {...{ isOpen, setIsOpen, reports, agentReports, selectedTabId: reportsId }} />
               {chartsData.charts[chartsData.mainChartId](reports, agentReports)}
            </>
         )}
      </Card>
   )
}

export default LivePanel
