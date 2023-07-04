import React from 'react'
import { styles } from './adaptation-value-indicator-styles'

interface Props {
   label: string
   value: string
}

/**
 * Component represents a value field used in adaptation actions modal
 *
 * @param {boolean}[label] - label displayed in the field
 * @param {func}[value] - value displayed inside the field
 * @returns JSX Element
 */
const AdaptationValueField = ({ label, value }: Props) => {
   const { valueFieldWrapper, valueFieldLabel, valueFieldValue } = styles

   return (
      <div style={valueFieldWrapper}>
         <div style={valueFieldLabel}>{label.toUpperCase()}</div>
         <div style={valueFieldValue}>{value}</div>
      </div>
   )
}

export default AdaptationValueField
