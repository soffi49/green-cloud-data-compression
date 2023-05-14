import { createSelector } from '@reduxjs/toolkit'
import { RootState } from 'store/store'

export const navigatorSelect = (state: RootState) => state.navigator

/**
 * Method returns current navigator state
 */
export const selectSelectedTab = createSelector([navigatorSelect], (navigatorSelect) => {
   console.warn(navigatorSelect.selectedTab)
   return navigatorSelect.selectedTab
})
