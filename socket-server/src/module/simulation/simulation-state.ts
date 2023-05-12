
interface SimulationState {
    systemStartTime: number | null
    secondsPerHour: number
}

let SIMULATION_STATE: SimulationState = {
    systemStartTime: null,
    secondsPerHour: 0,
}


const resetSimulationState = () =>
    Object.assign(SIMULATION_STATE,
        ({
            systemStartTime: null,
            secondsPerHour: 0,
        }))

export {
    SIMULATION_STATE,
    resetSimulationState
}