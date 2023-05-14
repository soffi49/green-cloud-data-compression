import { createSelector } from '@reduxjs/toolkit'
import { ClientAgentStatus } from '@types'
import { RootState } from 'store/store'

export const clientSelector = (state: RootState) => state.clients

/**
 * Method returns selected client id
 */
export const selectChosenClientId = createSelector([clientSelector], (agentSelector) => agentSelector.selectedClient)

/**
 * Method returns all clients
 */
export const selectClients = createSelector(
   [clientSelector],
   (agentSelector) => agentSelector.clients as ClientAgentStatus[]
)

/**
 * Method returns selected client
 */
export const selectChosenClient = createSelector([clientSelector], (agentSelector) => agentSelector.clientData)
