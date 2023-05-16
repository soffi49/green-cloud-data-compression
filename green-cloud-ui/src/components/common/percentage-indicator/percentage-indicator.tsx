import React from 'react'
import { styles } from './percentage-indicator-styles'
import { CircularProgressbar } from 'react-circular-progressbar'

interface Props {
   title: string
   value: number
}

/**
 * Component represents a circular indicator that with percentage value inside
 *
 * @param {number}[value] - percentage value
 * @param {string}[title] - title displayed over the indicator
 * @returns JSX Element
 */
const PercentageIndicator = ({ title, value }: Props) => {
   const {
      percentageIndicatorWrapper,
      percentageIndicatorTitle,
      percentageIndicatorContent,
      percentageIndicatorContainer,
   } = styles

   return (
      <div style={percentageIndicatorWrapper}>
         <div style={percentageIndicatorTitle}>{title.toUpperCase()}</div>
         <div style={percentageIndicatorContainer}>
            <CircularProgressbar
               {...{ value, text: `${value}%`, strokeWidth: 10, styles: percentageIndicatorContent }}
            />
         </div>
      </div>
   )
}

export default PercentageIndicator
