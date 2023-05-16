import React from 'react'
import { styles } from './value-indicator-styles'
import { IconElement } from '@types'

interface Props {
   title: string
   value: number
   icon?: IconElement
}
/**
 * Component represents a container that displays information about particular value and
 * optionally, a relevant icon
 *
 * @param {number}[value] - percentage value
 * @param {string}[title] - title displayed over the indicator
 * @param {React.ElementType}[icon] - icon dispayed next to the value
 * @returns JSX Element
 */
const LiveChartModal = ({ title, value, icon }: Props) => {
   const Icon = icon
   const { wrapper, container, titleStyle, valueStyle, iconStyle } = styles

   return (
      <div style={wrapper}>
         <div style={titleStyle}>{title.toUpperCase()}</div>
         <div style={container}>
            <div style={valueStyle}>{value}</div>
            {Icon && <Icon {...iconStyle} />}
         </div>
      </div>
   )
}

export default LiveChartModal
