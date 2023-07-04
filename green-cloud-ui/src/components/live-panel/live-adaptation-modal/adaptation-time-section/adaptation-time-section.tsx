import { AdaptationAction } from '@types'
import React from 'react'
import { styles } from './adaptation-time-section-styles'
import { convertMillisToString, getAverage } from '@utils'
import AdaptationValueField from '../adaptation-value-indicator/adaptation-value-indicator'

interface Props {
   adaptations: AdaptationAction[]
}

const headerTime = 'Adaptation execution duration'

/**
 * Component represents a section with adaptation executin duration statistics
 *
 * @param {AdaptationAction[]}[adaptations] - adaptation actions data
 * @returns JSX Element
 */
const AdaptationTimeSection = ({ adaptations }: Props) => {
   const { wrapper, headerWrapper, headerText, fieldsWrapper } = styles
   const adaptationTimes = adaptations
      .filter((adaptation) => adaptation.runsNo > 0)
      .map((adaptation) => adaptation.avgDuration)

   const durationFields = [
      { name: 'Avg', value: Math.round(getAverage(adaptationTimes)) },
      {
         name: 'Min',
         value: adaptationTimes.length > 0 ? Math.min(...adaptationTimes) : 0
      },
      {
         name: 'Max',
         value: adaptationTimes.length > 0 ? Math.max(...adaptationTimes) : 0
      }
   ]

   const getAverageFields = () =>
      durationFields.map((field) => {
         return (
            <AdaptationValueField
               {...{ key: field.name, label: field.name, value: convertMillisToString(field.value) }}
            />
         )
      })

   return (
      <div style={wrapper}>
         <div style={headerWrapper}>
            <div style={headerText}>{headerTime.toUpperCase()}</div>
         </div>
         <div style={fieldsWrapper}>{getAverageFields()}</div>
      </div>
   )
}

export default AdaptationTimeSection
