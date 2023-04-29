import React, { useEffect, useState } from 'react'
import { styles } from './main-view-style'
import { Menu, GraphPanel, MainPanel, LivePanel } from '@components'

interface Props {
   resetServerConnection: () => void
}

/**
 * Component representing main application view
 *
 * @returns JSX Element
 */
export const MainView = ({ resetServerConnection }: Props) => {
   const {
      mainContainer,
      menuContainer,
      contentContainer,
      mainPanelContainer,
      livePanelContainer,
      graphPanelContainer,
      leftSectionContainer,
      rightSectionContainer,
      sectionContainer,
   } = styles
   const [selectedTabId, setSelectedTabId] = useState<string>('cloud')

   useEffect(() => {
      resetServerConnection()
   }, [])

   return (
      <div style={mainContainer}>
         <div style={menuContainer}>
            <Menu {...{ changeTab: setSelectedTabId }} />
         </div>
         <div style={contentContainer}>
            <div style={{ ...sectionContainer, ...leftSectionContainer }}>
               <div style={mainPanelContainer}>
                  <MainPanel {...{ selectedTabId }} />
               </div>
               <div style={livePanelContainer}>
                  <LivePanel {...{ selectedTabId }} />
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
