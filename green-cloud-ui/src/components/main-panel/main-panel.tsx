import React from 'react'

import { styles } from './main-panel-styles'

import { Card } from '@components'
import { PANEL_TABS } from './main-panel-config'
import { Agent } from '@types'

interface Props {
   selectedTabId: string
   selectedAgent?: Agent
}

/**
 * Component represents a panel gathering all infromations about cloud network
 *
 * @param {string}[selectedTabId] - id of selected tab
 * @returns JSX Element
 */
export const MainPanel = ({ selectedTabId = 'cloud', selectedAgent }: Props) => {
   const { mainContainer } = styles
   const selectedTab = PANEL_TABS.filter((tab) => tab.id === selectedTabId)[0]
   const subHeader = selectedTabId === 'agents' ? selectedAgent?.name : undefined

   return (
      <Card
         {...{
            containerStyle: mainContainer,
            header: selectedTab.header,
            subHeader,
            removeScroll: selectedTab.removeScroll,
         }}
      >
         {selectedTab.panel}
      </Card>
   )
}

export default MainPanel
