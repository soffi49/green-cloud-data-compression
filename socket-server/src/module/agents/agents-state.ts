interface AgentsState {
    agents: any[]
}

interface AgentsReportsState {
    agentsReports: any[]
}


let AGENTS_STATE: AgentsState = {
    agents: []
}

let AGENTS_REPORTS_STATE: AgentsReportsState = {
    agentsReports: []
}

const resetAgentsState = () =>
    Object.assign(AGENTS_STATE,
        ({
            agents: []
        }))

const resetAgentsReportsState = () =>
    Object.assign(AGENTS_REPORTS_STATE,
        ({
            agentsReports: []
        }))


export {
    AGENTS_STATE,
    AGENTS_REPORTS_STATE,
    resetAgentsState,
    resetAgentsReportsState
}