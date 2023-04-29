import { createSelector } from '@reduxjs/toolkit'
import { ClientAgent, SchedulerAgent } from '@types'
import { RootState } from 'store/store'

export const agentSelector = (state: RootState) => state.agents

/**
 * Method returns selected network agent
 */
export const selectChosenNetworkAgent = createSelector([agentSelector], (agentSelector) =>
   agentSelector.agents.find((agent) => agent.name === agentSelector.selectedAgent)
)

/**
 * Method returns selected network agent id
 */
export const selectChosenNetworkAgentId = createSelector(
   [agentSelector],
   (agentSelector) => agentSelector.selectedAgent
)

/**
 * Method returns all clients
 */
export const selectClients = createSelector([agentSelector], (agentSelector) => agentSelector.clients as ClientAgent[])

/**
 * Method returns selected client
 */
export const selectChosenClient = createSelector(
   [agentSelector],
   (agentSelector) =>
      (agentSelector.clients.find(
         (agent) => agent.name.toUpperCase() === agentSelector.selectedClient
      ) as ClientAgent) ?? null
)

/**
 * Method returns scheduled jobs
 */
export const selectScheduledJobs = createSelector(
   [agentSelector],
   (agentSelector) =>
      (agentSelector.agents.find((agent) => agent.name === agentSelector.selectedAgent) as SchedulerAgent)
         .scheduledJobs ?? null
)
