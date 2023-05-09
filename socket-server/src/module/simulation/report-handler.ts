import { REPORTING_TIME } from "../../constants/index.js";
import { getCurrentTime } from "../../utils/index.js";
import { updateAgentsReportsState } from "../agents/index.js";
import { updateClientReportsState } from "../clients/index.js";
import { updateNetworkReportsState } from "../network/index.js";
import { SIMULATION_STATE } from "./simulation-state.js";

const reportSimulationStatistics = setInterval(function () {
    if (SIMULATION_STATE.systemStartTime !== null) {
        const time = getCurrentTime()

        updateAgentsReportsState(time)
        updateClientReportsState(time)
        updateNetworkReportsState(time)
    }
}, REPORTING_TIME * 1000);

export {
    reportSimulationStatistics
}
