import { AdaptationAction } from '@types'
import React from 'react'
import { styles } from './adaptation-trend-section-styles'
import { ValueIndicator } from 'components/common'
import { IconDecreaseArrow, IconIncreaseArrow } from '@assets'

interface Props {
   adaptations: AdaptationAction[]
}

const headerTime = 'Adaptation execution trends'

/**
 * Component represents a section with adaptation trend statistics
 *
 * @param {AdaptationAction[]}[adaptations] - adaptation actions data
 * @returns JSX Element
 */
const AdaptationTrendSection = ({ adaptations }: Props) => {
   const { wrapper, headerWrapper, headerText, fieldsWrapper, valueFieldContainer } = styles

   const isAnyAdaptationExecuted = adaptations.some((adaptation) => adaptation.runsNo > 0)

   const mostFrequentAdaptation =
      adaptations.length > 0 && isAnyAdaptationExecuted
         ? adaptations.reduce((prev, curr) => (prev.runsNo > curr.runsNo ? prev : curr), adaptations[0]).name
         : '-'

   const leastFrequentAdaptation =
      adaptations.length > 0 && isAnyAdaptationExecuted
         ? adaptations.reduce((prev, curr) => (prev.runsNo < curr.runsNo ? prev : curr), adaptations[0]).name
         : '-'

   return (
      <div style={wrapper}>
         <div style={headerWrapper}>
            <div style={headerText}>{headerTime.toUpperCase()}</div>
         </div>
         <div style={fieldsWrapper}>
            <ValueIndicator
               {...{
                  title: 'Most executed action',
                  value: mostFrequentAdaptation,
                  icon: IconIncreaseArrow,
                  wrapperStyle: valueFieldContainer
               }}
            />
            <ValueIndicator
               {...{
                  title: 'Least executed action',
                  value: leastFrequentAdaptation,
                  icon: IconDecreaseArrow,
                  wrapperStyle: valueFieldContainer
               }}
            />
         </div>
      </div>
   )
}

export default AdaptationTrendSection
