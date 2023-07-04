import { AdaptationAction, AdaptationGoal, AgentStatisticReport, LiveChartDashboardType, ReportsStore } from '@types'
import Modal from 'components/common/modal/modal'
import React from 'react'
import { CHART_MODALS } from '../config/live-panel-config'
import { styles } from './live-adaptation-modal-styles'
import AdaptationTimeSection from './adaptation-time-section/adaptation-time-section'
import AdaptationTrendSection from './adaptation-trend-section/adaptation-trend-section'
import AdaptationActionTable from './adaptation-action-table/adaptation-action-table'
import AdaptationChartSection from './adaptation-chart-section/adaptation-chart-section'
import AdaptationGoalSection from './adaptation-goal-section/adaptation-goal-section'

interface Props {
   isOpen: boolean
   setIsOpen: (state: boolean) => void
   adaptations: AdaptationAction[]
   systemGoals: AdaptationGoal[]
   reports: ReportsStore
   agentReports: AgentStatisticReport | null
}

const headerIndicators = 'Adaptation execution general statistics'
const headerDashboard = 'Adaptation execution details'
const headerAvgGoals = 'Average system goals value'

/**
 * Component represents a pop-up modal displaying all reports related to the given tab
 *
 * @param {boolean}[isOpen] - flag indicating if the modal is currently open
 * @param {func}[setIsOpen] - function changing the state of the modal
 * @param {AdaptationAction[]}[adaptations] - adaptation actions data
 * @param {ReportsStore}[reports] - system reports
 * @param {AdaptationGoal[]}[systemGoals] - goals defined in the system
 *
 * @returns JSX Element
 */
const LiveAdaptationModal = ({ isOpen, setIsOpen, reports, adaptations, systemGoals }: Props) => {
   const {
      modalStyle,
      modalHeader,
      wrapper,
      indicatorsContent,
      contentWrapper,
      contentContainer,
      headerContainer,
      headerText
   } = styles
   const { name: header } = CHART_MODALS['adaptation'] as LiveChartDashboardType

   return (
      <Modal
         {...{
            isOpen,
            setIsOpen,
            contentStyle: modalStyle,
            header: `LIVE STATISTICS - ${header}`,
            disableScroll: true,
            headerStyle: modalHeader
         }}
      >
         <div style={wrapper}>
            <div style={contentWrapper}>
               <div style={headerContainer}>
                  <div style={headerText}>{headerAvgGoals.toUpperCase()}</div>
               </div>
               <div style={contentContainer}>
                  <AdaptationGoalSection {...{ reports }} />
               </div>
            </div>
            <div style={{ ...contentWrapper, gridColumn: 2, gridRow: '1/ span 2' }}>
               <div style={headerContainer}>
                  <div style={headerText}>{headerDashboard.toUpperCase()}</div>
               </div>
               <div style={contentContainer}>
                  <AdaptationChartSection {...{ reports }} />
                  <AdaptationActionTable {...{ adaptations, systemGoals }} />
               </div>
            </div>
            <div style={contentWrapper}>
               <div style={headerContainer}>
                  <div style={headerText}>{headerIndicators.toUpperCase()}</div>
               </div>
               <div style={indicatorsContent}>
                  <AdaptationTimeSection {...{ adaptations }} />
                  <AdaptationTrendSection {...{ adaptations }} />
               </div>
            </div>
            {/* <div style={contentWrapper}>
               <div style={headerContainer}>
                  <div style={headerText}>{headerTable.toUpperCase()}</div>
               </div>
               <div style={contentContainer}>
                  <AdaptationActionTable {...{ adaptations, systemGoals }} />
               </div>
            </div> */}
         </div>
      </Modal>
   )
}

export default LiveAdaptationModal
