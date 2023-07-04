import { LiveIndicatorAvgGenerator, ReportsStore } from '@types'
import { getAverage } from '@utils'
import { getJobStatusDuration } from 'utils/job-utils'

const getSystemAvgTrafficIndicator: LiveIndicatorAvgGenerator = (reports) =>
   Math.round(getAverage((reports as ReportsStore).systemTrafficReport, 'value'))

const getSystemAvgClientsIndicator: LiveIndicatorAvgGenerator = (reports) =>
   Math.round(getAverage((reports as ReportsStore).clientsReport, 'value'))

const getSystemAvgJobsIndicator: LiveIndicatorAvgGenerator = (reports) =>
   Math.round(getAverage((reports as ReportsStore).executedJobsReport, 'value'))

const getAvgInProgressJobTime: LiveIndicatorAvgGenerator = (reports) => {
   const { clientsStatusReport } = reports as ReportsStore
   const inProgressValues = clientsStatusReport
      .map((entry) => entry.value)
      .flatMap((entry) => entry)
      .filter((entry) => entry.status === 'IN_PROGRESS')
   const avgInProgress = Math.round(getAverage(inProgressValues, 'value'))

   return getJobStatusDuration('IN_PROGRESS', avgInProgress)
}

const getAverageJobExecutionPercentage: LiveIndicatorAvgGenerator = (reports) =>
   Math.round((reports as ReportsStore).avgClientsExecutionPercentage.slice(-1)[0]?.value ?? 0)

export {
   getSystemAvgClientsIndicator,
   getSystemAvgJobsIndicator,
   getSystemAvgTrafficIndicator,
   getAverageJobExecutionPercentage,
   getAvgInProgressJobTime
}
