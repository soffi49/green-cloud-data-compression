import { Agent, ClientAgentStatus } from 'types/agents'

export interface DropdownOption {
   value: Agent | ClientAgentStatus | null | string | number
   label: string
   isSelected?: boolean
}
