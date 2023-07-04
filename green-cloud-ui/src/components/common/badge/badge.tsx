import React from 'react'
import { styles } from './badge-styles'

interface Props {
   text: string
   isActive?: boolean
   color?: string
   isSmall?: boolean
}

/**
 * Component representing a text badge
 *
 * @param {string}[text] - text to be displayes inside the badge
 * @param {boolean}[isActive] - (optional) state of the badge
 * @param {string}[color] - (optional) color of the badge
 * @param {boolean}[isSmall] - optional flag indicating if the badge is of smaller size
 *
 * @returns JSX Element
 */
const Badge = ({ text, isActive, color, isSmall }: Props) => {
   const badgeStyle = isActive ? styles.activeBadge : styles.inActiveBadge
   const sizedBadge = isSmall ? { ...badgeStyle, fontSize: 'var(--font-size-7)', padding: '5px 10px' } : badgeStyle
   const style = color ? { ...styles.badge, ...sizedBadge, backgroundColor: color } : { ...styles.badge, ...sizedBadge }

   return <span {...{ style }}>{text}</span>
}

export default Badge
