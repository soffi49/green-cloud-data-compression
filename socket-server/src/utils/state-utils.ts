import {
    AGENTS_REPORTS_STATE,
    AGENTS_STATE,
    CLIENTS_REPORTS_STATE,
    CLIENTS_STATE,
    GRAPH_STATE,
    MANAGING_SYSTEM_STATE,
    NETWORK_REPORTS_STATE,
    NETWORK_STATE,
    resetAgentsReportsState,
    resetAgentsState,
    resetClientsReportsState,
    resetClientsState,
    resetGraphState,
    resetManagingSystemState,
    resetNetworkReportsState,
    resetNetworkState,
    resetSimulationState,
    SIMULATION_STATE
} from "../module"

const resetSystemState = () => {
    resetAgentsReportsState()
    resetAgentsState()
    resetGraphState()
    resetClientsReportsState()
    resetClientsState()
    resetManagingSystemState()
    resetNetworkReportsState()
    resetNetworkState()
    resetSimulationState()
}

const getSystemState = () => {
    return ({
        systemStartTime: SIMULATION_STATE.systemStartTime,
        secondsPerHour: SIMULATION_STATE.systemStartTime,
        network: { ...NETWORK_STATE },
        agents: {
            ...CLIENTS_STATE,
            ...AGENTS_STATE
        },
        managingSystem: { ...MANAGING_SYSTEM_STATE },
        graph: { ...GRAPH_STATE }
    })
}

const getAgentsState = () => {
    return ({
        ...AGENTS_STATE ,
        ...AGENTS_REPORTS_STATE,
        graph: { ...GRAPH_STATE }
    })
}

const getClientsState = () => {
    return ({
        ...CLIENTS_STATE,
        clientsReports: { ...CLIENTS_REPORTS_STATE },
    })
}

const getManagingState = () => {
    return ({
        managing: { ...MANAGING_SYSTEM_STATE },
    })
}

const getNetworkState = () => {
    return ({
        network: { ...NETWORK_STATE },
        networkReport: {...NETWORK_REPORTS_STATE},
        simulation: {...SIMULATION_STATE}
    })
}

const getReportsState = () => {
    return ({
        ...NETWORK_REPORTS_STATE,
        ...CLIENTS_REPORTS_STATE,
        ...AGENTS_REPORTS_STATE
    })
}

export {
    resetSystemState,
    getSystemState,
    getAgentsState,
    getClientsState,
    getManagingState,
    getNetworkState,
    getReportsState
}