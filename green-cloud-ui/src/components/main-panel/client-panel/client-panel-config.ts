import { DropdownOption, JobStatus } from '@types'

export const CLIENT_STATISTICS = [
   { key: 'jobId', label: 'Job identifier' },
   { key: 'status', label: 'Job status' },
   { key: 'power', label: 'Power used for job' },
   { key: 'start', label: 'Start date' },
   { key: 'end', label: 'End date' },
   { key: 'deadline', label: 'Job execution deadline' },
   { key: 'durationMap', label: 'Job status duration map' },
   { key: 'jobExecutionProportion', label: 'Job execution percentage' }
]

export const SPLIT_JOB_STATISTICS = [
   { key: 'status', label: 'Job status' },
   { key: 'power', label: 'Power used for job' },
   { key: 'start', label: 'Start date' },
   { key: 'end', label: 'End date' }
]

export const CLIENTS_ORDER = [
   JobStatus.CREATED.toString(),
   JobStatus.PROCESSED.toString(),
   JobStatus.IN_PROGRESS.toString(),
   JobStatus.ON_BACK_UP.toString(),
   JobStatus.ON_HOLD.toString(),
   JobStatus.DELAYED.toString(),
   JobStatus.REJECTED.toString(),
   JobStatus.FINISHED.toString(),
   JobStatus.FAILED.toString()
]

export const INITIAL_JOB_SPLIT_SELECT_OPTIONS: DropdownOption[] = [
   { value: 'JOBS SPLIT TO PARTS', label: 'JOBS SPLIT TO PARTS', isSelected: false },
   { value: 'JOBS EXECUTED AS WHOLE', label: 'JOBS SPLIT TO PARTS', isSelected: false },
   { value: 'ALL JOBS', label: 'ALL JOBS', isSelected: true }
]

export const ALL_STATUS: DropdownOption = {
   value: 'SELECT ALL STATUSES',
   label: 'SELECT ALL STATUSES',
   isSelected: true
}

export const JOB_STATUS_MAP = Object.values(JobStatus).map((key) => {
   return { value: key as string, label: key as string, isSelected: true }
})
