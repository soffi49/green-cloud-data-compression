import { SIMULATION_STATE } from "./simulation-state"

const handleSystemTimeMesage = (msg) => {
    if (SIMULATION_STATE.systemStartTime === null) {
        SIMULATION_STATE.systemStartTime = msg.time
        SIMULATION_STATE.secondsPerHour = msg.secondsPerHour
    }
}

export {
    handleSystemTimeMesage
}