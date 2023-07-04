export type CloudNetworkStore = {
   currClientsNo: number
   currActiveJobsNo: number
   currPlannedJobsNo: number
   finishedJobsNo: number
   failedJobsNo: number
   isNetworkSocketConnected?: boolean | null
   isAgentSocketConnected?: boolean | null
   isClientSocketConnected?: boolean | null
   isAdaptationSocketConnected?: boolean | null
   connectionToast: boolean
}
