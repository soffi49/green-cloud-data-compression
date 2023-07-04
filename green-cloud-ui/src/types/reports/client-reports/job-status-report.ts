import { JobStatus } from 'types/enum'

export interface JobStatusReport {
   status: JobStatus | string
   value: number
}
