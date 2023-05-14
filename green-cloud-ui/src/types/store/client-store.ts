import { ClientAgent, ClientAgentStatus } from 'types/agents'

export type ClientStoreState = {
   clients: ClientAgentStatus[]
   clientData: ClientAgent | null
   selectedClient: string | null
}
