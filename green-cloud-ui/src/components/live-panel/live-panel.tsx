import React, { useState } from 'react'

import { styles } from './live-panel-styles'

import { Button, Card, SubtitleContainer } from '@components'
import { AgentStatisticReport, ReportsStore } from '@types'
import { CHARTS, CHART_MODALS } from './live-panel-config'
import LiveChartModal from './live-chart-modal/live-chart-modal'

interface Props {
   selectedTabId: string
   reports: ReportsStore
   agentReports: AgentStatisticReport | null
}

const headerTitle = 'Live statistics'
const buttonTitle = 'Display all statistics'

/**
 * Component represents a panel gathering all infromations about cloud network
 *
 * @param {string}[selectedTabId] - id of selected tab
 * @returns JSX Element
 */
export const LivePanel = ({ selectedTabId = 'cloud', reports, agentReports }: Props) => {
   const [isOpen, setIsOpen] = useState<boolean>(false)
   const { mainContainer, contentContainer, headerContainer, headerStyle } = styles

   const isAgentTab = selectedTabId === 'agents'
   const hasData = reports.clientsReport.length !== 0
   const reportsId = isAgentTab && agentReports ? `agent${agentReports?.type}` : selectedTabId

   const buttonClassName = [
      'medium-green-button',
      hasData && CHART_MODALS[reportsId] ? 'medium-green-button-active' : 'medium-green-button-disabled',
   ].join(' ')

   const header = (
      <div style={headerContainer}>
         <div style={headerStyle}>{headerTitle.toUpperCase()}</div>
         <Button {...{ title: buttonTitle.toUpperCase(), onClick: () => setIsOpen(!isOpen), buttonClassName }} />
      </div>
   )

   return (
      <Card {...{ containerStyle: mainContainer, contentStyle: contentContainer, header, removeScroll: true }}>
         {!hasData || CHART_MODALS[reportsId] === undefined || (isAgentTab && !agentReports) ? (
            <SubtitleContainer text="No data available yet" />
         ) : (
            <>
               <LiveChartModal {...{ isOpen, setIsOpen, reports, agentReports, selectedTabId: reportsId }} />
               {CHARTS.systemTraffic(reports)}
            </>
         )}
      </Card>
   )
}

export default LivePanel
