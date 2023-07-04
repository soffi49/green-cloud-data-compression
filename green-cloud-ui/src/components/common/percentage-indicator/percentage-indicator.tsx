import React from 'react'
import { styles } from './percentage-indicator-styles'
import { CircularProgressbar } from 'react-circular-progressbar'

interface Props {
   title: string
   value: number | string
   width?: string
}

/**
 * Component represents a circular indicator that with percentage value inside
 *
 * @param {number}[value] - percentage value
 * @param {string}[title] - title displayed over the indicator
 * @param {string}[width] - optional width of the percentage indicator cycle
 * @returns JSX Element
 */
const PercentageIndicator = ({ title, value, width }: Props) => {
   const {
      percentageIndicatorWrapper,
      percentageIndicatorTitle,
      percentageIndicatorContent,
      percentageIndicatorContainer
   } = styles

   const containerStyle = width ? { ...percentageIndicatorContainer, width } : percentageIndicatorContainer

   return (
      <div style={percentageIndicatorWrapper}>
         <div style={percentageIndicatorTitle}>{title.toUpperCase()}</div>
         <div style={containerStyle}>
            <CircularProgressbar
               {...{ value: value as number, text: `${value}%`, strokeWidth: 10, styles: percentageIndicatorContent }}
            />
         </div>
      </div>
   )
}

export default PercentageIndicator
