import { IconElement } from 'types/assets'
import { ReportEventTypes } from './report-event-types'

export interface ReportEvents {
   type: ReportEventTypes
   time: number
   name: string
   description?: string
   icon?: IconElement
}
