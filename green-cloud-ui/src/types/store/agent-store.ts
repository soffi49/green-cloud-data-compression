import { Agent } from '../agents/agent-interface'

export type AgentStore = {
   agents: Agent[]
   selectedAgent: string | null
}
