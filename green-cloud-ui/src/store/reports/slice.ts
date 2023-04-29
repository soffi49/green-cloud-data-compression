import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { ReportsStore, SystemTimeMessage } from '@types'

const INITIAL_STATE: ReportsStore = {
   systemStartTime: null,
   secondsPerHour: null,
   finishJobsReport: [],
   failJobsReport: [],
   executedJobsReport: [],
   systemTrafficReport: [],
   clientsReport: [],
   avgJobSizeReport: [],
   minJobSizeReport: [],
   maxJobSizeReport: [],
   agentsReports: [],
}

/**
 * Slice storing current report data of the system
 */
export const reportsSlice = createSlice({
   name: 'reports',
   initialState: INITIAL_STATE,
   reducers: {
      updateReports(state, action: PayloadAction<ReportsStore>) {
         Object.assign(state, action.payload)
      },
      updateSystemTime(state, action: PayloadAction<SystemTimeMessage>) {
         state.secondsPerHour = action.payload.secondsPerHour
         state.systemStartTime = action.payload.time
      },
      resetReports(state) {
         Object.assign(state, INITIAL_STATE)
      },
   },
})
