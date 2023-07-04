import React, { useEffect } from 'react'
import { styles } from './main-view-style'
import { Menu, GraphPanel, MainPanel, LivePanel } from '@components'
import { MenuTab } from '@types'

interface Props {
   resetServerConnection: () => void
   setSelectedTab: (tab: MenuTab) => void
}

/**
 * Component representing main application view
 *
 * @returns JSX Element
 */
export const MainView = ({ resetServerConnection, setSelectedTab }: Props) => {
   const {
      mainContainer,
      menuContainer,
      contentContainer,
      mainPanelContainer,
      livePanelContainer,
      graphPanelContainer,
      leftSectionContainer,
      rightSectionContainer,
      sectionContainer
   } = styles

   useEffect(() => {
      resetServerConnection()
   }, [])

   const changeTab = (tab: MenuTab) => {
      setSelectedTab(tab)
   }

   return (
      <div style={mainContainer}>
         <div style={menuContainer}>
            <Menu {...{ changeTab }} />
         </div>
         <div style={contentContainer}>
            <div style={{ ...sectionContainer, ...leftSectionContainer }}>
               <div style={mainPanelContainer}>
                  <MainPanel />
               </div>
               <div style={livePanelContainer}>
                  <LivePanel />
               </div>
            </div>
            <div style={{ ...sectionContainer, ...rightSectionContainer }}>
               <div style={graphPanelContainer}>
                  <GraphPanel />
               </div>
            </div>
         </div>
      </div>
   )
}
