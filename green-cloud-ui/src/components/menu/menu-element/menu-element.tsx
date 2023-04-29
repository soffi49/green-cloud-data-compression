import React from 'react'
import { styles } from './menu-element-styles'
import { Tooltip } from 'react-tooltip'

interface Props {
   header: string
   icon: React.ReactNode
   iconOffset: number
   id: string
   changeTab: React.Dispatch<React.SetStateAction<string>>
}

/**
 * Component representing the menu element
 *
 * @param {string} header - header text
 * @param {string} id - identifier of the tooltip
 * @param {object} icon - icon to be displayed
 * @param {func}[changeTab] - function that changes displayed tabs
 * @returns JSX Element
 */
const MenuElement = ({ header, icon, id, iconOffset, changeTab }: Props) => {
   const { menuIcon, menuIndicator, menuIndicatorText } = styles

   return (
      <div>
         <Tooltip style={menuIndicator} id={id} place={'right'} delayShow={0} noArrow>
            <span style={menuIndicatorText}>{header.toUpperCase()}</span>
         </Tooltip>
         <div
            {...{
               style: menuIcon,
               'data-tooltip-id': id,
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
