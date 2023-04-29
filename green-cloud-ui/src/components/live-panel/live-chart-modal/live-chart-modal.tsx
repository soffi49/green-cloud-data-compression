import { AgentStatisticReport, ReportsStore } from '@types'
import Modal from 'components/common/modal/modal'
import React from 'react'
import { CHART_MODALS } from '../live-panel-config'
import { styles } from './live-chart-modal-styles'

interface Props {
   isOpen: boolean
   setIsOpen: (state: boolean) => void
   selectedTabId: string
   reports: ReportsStore
   agentReports: AgentStatisticReport | null
}

const headerReal = 'Real time statistics'
const headerAverage = 'Average statistics'

/**
 * Component represents a pop-up modal displaying all reports related to the given tab
 *
 * @param {boolean}[isOpen] - flag indicating if the modal is currently open
 * @param {func}[setIsOpen] - function changing the state of the modal
 * @param {string}[selectedTabId] - identifier of currently selected tab
 * @param {ReportsStore}[reports] - system reports
 * @param {AgentStatisticReport | null}[agentReports] - reports of selected agent
 * @returns JSX Element
 */
const LiveChartModal = ({ isOpen, setIsOpen, reports, agentReports, selectedTabId = 'cloud' }: Props) => {
   const {
      modalStyle,
      modalHeader,
      wrapper,
      chartContentWrapper,
      chartContainerWrapper,
      headerContainer,
      headerText,
      chartWrapper,
      avgContainerWrapper,
      avgContentWrapper,
   } = styles
   const systemReports = CHART_MODALS[selectedTabId]
   const header = selectedTabId.startsWith('agents')
      ? `${agentReports?.name} ${systemReports.name.toUpperCase()}`
      : systemReports.name.toUpperCase()

   const getValueFields = () =>
      systemReports.valueFields?.map((configuration) => {
         const Field = configuration.indicator
         const icon = configuration.icon
         console.error(configuration.value(reports))
         return <Field {...{ title: configuration.title, value: configuration.value(reports), icon }} />
      })

   const getCharts = () =>
      systemReports.charts.map((chartGenerator, idx) => {
         const isOddLast = idx === systemReports.charts.length - 1 && idx % 2 === 0
         const styleWrapper = { ...chartWrapper, ...{ gridColumn: isOddLast ? '1/-1' : undefined } }
         return <div style={styleWrapper}>{chartGenerator(reports, agentReports)}</div>
      })

   return (
      <Modal
         {...{
            isOpen,
            setIsOpen,
            contentStyle: modalStyle,
            header: `LIVE STATISTICS - ${header}`,
            disableScroll: true,
            headerStyle: modalHeader,
         }}
      >
         <div style={wrapper}>
            <div style={avgContentWrapper}>
               <div style={headerContainer}>
                  <div style={headerText}>{headerAverage.toUpperCase()}</div>
               </div>
               <div style={avgContainerWrapper}>{getValueFields()}</div>
            </div>
            <div style={chartContentWrapper}>
               <div style={headerContainer}>
                  <div style={headerText}>{headerReal.toUpperCase()}</div>
               </div>
               <div style={chartContainerWrapper}>{getCharts()}</div>
            </div>
         </div>
      </Modal>
   )
}

export default LiveChartModal
