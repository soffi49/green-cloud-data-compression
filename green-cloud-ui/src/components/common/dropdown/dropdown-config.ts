import { Agent, ClientAgentStatus } from '@types'

export interface GroupedOption {
   label: string
   options: SelectOption[]
}

export interface SelectOption {
   value: Agent | ClientAgentStatus | null | string | number
   label: string
   isSelected?: boolean
}

export const MAX_DROPDOWN_HEIGHT = 150
export const MIN_DROPDOWN_HEIGHT = 0
