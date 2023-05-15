import { Agent } from '../agents/agent-interface'

export type AgentStore = {
   agents: Agent[]
   agentData: Agent | null
   selectedAgent: string | null
}
