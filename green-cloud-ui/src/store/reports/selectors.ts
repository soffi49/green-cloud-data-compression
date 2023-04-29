import { createSelector } from '@reduxjs/toolkit'
import { agentSelector } from 'store/agent'
import { RootState } from 'store/store'

const reportsSelector = (state: RootState) => state.reports

/**
 * Method returns data used to generate reports
 */
export const selectReports = createSelector([reportsSelector], (reportsSelector) => reportsSelector)

/**
 * Method returns reports for given agent
 */
export const selectReportsForChosenAgent = createSelector(
   [reportsSelector, agentSelector],
   (reportsSelector, agentSelector) => {
      const { selectedAgent } = agentSelector
      return !selectedAgent ? null : reportsSelector.agentsReports.filter((report) => report.name === selectedAgent)[0]
   }
)

/**
 * Method returns system start time
 */
export const selectSystemStartTime = createSelector(
   [reportsSelector],
   (reportsSelector) => reportsSelector.systemStartTime
)

/**
 * Method returns number of seconds in simmulation time that corresponds to 1 hour
 */
export const selectSecondsPerHour = createSelector(
   [reportsSelector],
   (reportsSelector) => reportsSelector.secondsPerHour
)
