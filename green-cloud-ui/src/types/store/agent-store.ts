import { Agent } from '../agents/agent-interface'

export type AgentStore = {
   agents: Agent[]
   clients: Agent[]
   selectedAgent: string | null
   selectedClient: string | null
}
