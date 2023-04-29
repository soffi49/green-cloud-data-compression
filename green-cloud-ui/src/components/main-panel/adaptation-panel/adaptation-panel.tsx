import React, { useState } from 'react'

import { styles } from './adaptation-panel-styles'
import { INITIAL_TABS } from './adaptation-panel-config'
import Navigator from 'components/common/navigator/navigator'

/**
 * Component represents a panel gathering all tabs related to the system's adaptations
 *
 * @returns JSX Element
 */
const AdaptationPanel = () => {
   const [selectedTabIdx, setSelectedTabIdx] = useState<number>(0)
   const { adaptationContainer } = styles

   const selectedTab = INITIAL_TABS[selectedTabIdx].panel

   return (
      <div style={adaptationContainer}>
         <Navigator
            {...{
               tabTitles: INITIAL_TABS.map((tab) => tab.title),
               selectedTabIdx,
               setSelectedTabIdx,
            }}
         />
         {selectedTab}
      </div>
   )
}

export default AdaptationPanel
