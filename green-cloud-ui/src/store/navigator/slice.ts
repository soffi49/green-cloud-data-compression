import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { MenuTab, NavigatorStoreState } from '@types'

const INITIAL_STATE: NavigatorStoreState = {
   selectedTab: MenuTab.CLOUD_SUMMARY
}

/**
 * Slice storing current navigator state
 */
export const navigatorSlice = createSlice({
   name: 'navigator',
   initialState: INITIAL_STATE,
   reducers: {
      setSelectedTab(state, action: PayloadAction<MenuTab>) {
         state.selectedTab = action.payload
      },
      resetNavigatorState(state) {
         Object.assign(state, INITIAL_STATE)
      }
   }
})
