import { ReportsStore } from '@types'
import React from 'react'
import { styles } from './adaptation-goal-section-styles'
import { PercentageIndicator } from 'components/common'
import { getAverage } from '@utils'

interface Props {
   reports: ReportsStore
}

/**
 * Component represents a section that embedds percentage indicators with goal qualities values
 *
 * @param {ReportsStore}[reports] - system reports
 *
 * @returns JSX Element
 */
const AdaptationGoalSection = ({ reports }: Props) => {
   const { wrapper, indicatorWrapper } = styles

   const avgSuccessRatio = getAverage(reports.jobSuccessRatioReport, 'value')
   const avgTrafficDist = getAverage(reports.trafficDistributionReport, 'value')
   const avgBackUp = getAverage(reports.backUpPowerUsageReport, 'value')

   return (
      <div style={wrapper}>
         <div style={indicatorWrapper}>
            <PercentageIndicator
               {...{
                  value: Math.round(avgSuccessRatio),
                  title: 'Job success ratio',
                  width: '90%'
               }}
            />
         </div>
         <div style={indicatorWrapper}>
            <PercentageIndicator
               {...{
                  value: Math.round(avgTrafficDist),
                  title: 'Traffic distribution',
                  width: '90%'
               }}
            />
         </div>
         <div style={indicatorWrapper}>
            <PercentageIndicator
               {...{
                  value: Math.round(avgBackUp),
                  title: 'Back-up power usage',
                  width: '90%'
               }}
            />
         </div>
      </div>
   )
}

export default AdaptationGoalSection
