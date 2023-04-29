import React from 'react'
import { styles } from './header-styles'

interface Props {
   text: string
}

/**
 * Component represents simple generic header
 *
 * @param {string}[text] - text displayed in the header
 * @returns JSX Element
 */
const Header = ({ text }: Props) => {
   return <div style={styles.headerStyle}>{text.toUpperCase()}</div>
}

export default Header
