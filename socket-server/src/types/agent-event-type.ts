import { EVENT_STATE, EVENT_TYPE } from "../constants/constants"

export interface AgentEvent {
    disabled: boolean
    state: EVENT_STATE
    type: EVENT_TYPE
 }