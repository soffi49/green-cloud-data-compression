import React from 'react'
import { styles } from './menu-element-styles'
import { Tooltip } from 'react-tooltip'
import { MenuTab } from '@types'

interface Props {
   header: string
   icon: React.ReactNode
   iconOffset: number
   id: MenuTab
   changeTab: (tab: MenuTab) => void
}

/**
 * Component representing the menu element
 *
 * @param {string} header - header text
 * @param {MenuTab} id - identifier of the tooltip
 * @param {object} icon - icon to be displayed
 * @param {func}[changeTab] - function that changes displayed tabs
 * @returns JSX Element
 */
const MenuElement = ({ header, icon, id, iconOffset, changeTab }: Props) => {
   const { menuIcon, menuIndicator, menuIndicatorText } = styles

   return (
      <div>
         <Tooltip style={menuIndicator} id={id.toString()} place={'right'} delayShow={0} noArrow>
            <span style={menuIndicatorText}>{header.toUpperCase()}</span>
         </Tooltip>
         <div
            {...{
               style: menuIcon,
               'data-tooltip-id': id.toString(),
               'data-tooltip-offset': iconOffset,
               onClick: () => changeTab(id),
            }}
         >
            {icon}
         </div>
      </div>
   )
}

export default MenuElement
