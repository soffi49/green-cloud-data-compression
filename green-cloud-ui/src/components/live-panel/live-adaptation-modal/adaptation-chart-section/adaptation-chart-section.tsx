import { LiveChartDashboardType, ReportsStore } from '@types'
import React from 'react'
import { styles } from './adaptation-chart-section-styles'
import { CHART_MODALS } from 'components/live-panel/config/live-panel-config'

interface Props {
   reports: ReportsStore
}

/**
 * Component embedding all charts of the adaptation dashboard
 *
 * @param {ReportsStore}[reports] - system reports
 *
 * @returns JSX Element
 */
const AdaptationChartSection = ({ reports }: Props) => {
   const { chartContainerWrapper, chartWrapper } = styles
   const { charts } = CHART_MODALS['adaptation'] as LiveChartDashboardType

   const getCharts = () =>
      charts.map((chartGenerator, idx) => {
         const isOddLast = idx === charts.length - 1 && idx % 2 === 0
         const styleWrapper = {
            ...chartWrapper,
            ...{ gridColumn: isOddLast ? '1/-1' : undefined }
         }
         return (
            <div key={'chart-' + idx} style={styleWrapper}>
               {chartGenerator(reports, null)}
            </div>
         )
      })

   return <div style={chartContainerWrapper}>{getCharts()}</div>
}

export default AdaptationChartSection
