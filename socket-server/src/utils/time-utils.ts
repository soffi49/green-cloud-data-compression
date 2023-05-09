import { SIMULATION_STATE } from "../module/simulation/simulation-state"

const getCurrentTime = () => {
    if (SIMULATION_STATE.systemStartTime !== null) {
        const timeDiff = new Date().getTime() - SIMULATION_STATE.systemStartTime
        const timeMultiplier = 3600 / SIMULATION_STATE.secondsPerHour;
        const realTimeDifference = timeDiff * timeMultiplier;

        return SIMULATION_STATE.systemStartTime + Math.round(realTimeDifference)
    }
}

export {
    getCurrentTime
}