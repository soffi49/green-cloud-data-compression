import { JobStatusReport } from './job-status-report'

export interface ClientsStatusReport {
   time: number
   value: JobStatusReport[]
}
