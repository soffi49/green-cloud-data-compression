import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { Agent, AgentStore, ClientAgent, PowerShortageEventData } from '@types'
import { getAgentByName, triggerPowerShortage } from './api'

const INITIAL_STATE: AgentStore = {
   agents: [],
   clients: [],
   selectedAgent: null,
   selectedClient: null,
}

/**
 * Slice storing current state of cloud network agents
 */
export const agentSlice = createSlice({
   name: 'agents',
   initialState: INITIAL_STATE,
   reducers: {
      triggerPowerShortage(state, action: PayloadAction<PowerShortageEventData>) {
         const { agentName, newMaximumCapacity } = action.payload

         if (getAgentByName(state.agents, agentName)) {
            triggerPowerShortage(agentName, newMaximumCapacity)
         }
      },
      setAgentsData(state, action: PayloadAction<Agent[]>) {
         Object.assign(state, { ...state, agents: action.payload })
      },
      setClientsData(state, action: PayloadAction<ClientAgent[]>) {
         Object.assign(state, { ...state, clients: action.payload })
      },
      setSelectedAgent(state, action: PayloadAction<string>) {
         Object.assign(state, { ...state, selectedAgent: action.payload })
      },
      setSelectedClient(state, action: PayloadAction<string | null>) {
         state.selectedClient = action.payload
      },
      resetAgents(state) {
         Object.assign(state, INITIAL_STATE)
      },
   },
})
