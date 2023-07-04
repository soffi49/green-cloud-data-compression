import { getAgentNodeById } from "../../utils/agent-utils";
import { GRAPH_STATE } from "../graph/graph-state";
import { AdaptationAction, AdaptationGoalQuality, MANAGING_SYSTEM_STATE } from "./managing-system-state";

const handleRegisterGoals = (msg) => (MANAGING_SYSTEM_STATE.adaptationGoals = msg.data);
const handleUpdateIndicators = (msg) => {
	const goalQualities = Object.entries(msg.data).map(
		([key, value]): AdaptationGoalQuality => ({ id: parseInt(key as string), quality: value as number })
	);

	MANAGING_SYSTEM_STATE.systemIndicator = msg.systemIndicator;
	MANAGING_SYSTEM_STATE.goalQualityIndicators = goalQualities;
};

const handleUpdateAdaptationAction = (msg) => {
	const actionData: AdaptationAction = msg.data;
	const actionToReplace = MANAGING_SYSTEM_STATE.adaptationActions.filter(
		(action) => action.name === actionData.name && action.goal === actionData.goal
	);

	if (actionToReplace.length > 0) {
		MANAGING_SYSTEM_STATE.adaptationActions = MANAGING_SYSTEM_STATE.adaptationActions.map((action) =>
			actionToReplace.includes(action) ? actionData : action
		);
	} else {
		MANAGING_SYSTEM_STATE.adaptationActions.push(actionData);
	}
};

const handleIncrementWeakAdaptations = (_msg) => {
	MANAGING_SYSTEM_STATE.weakAdaptations += 1;
	MANAGING_SYSTEM_STATE.performedAdaptations += 1;
};

const handleIncrementStrongAdaptations = (_msg) => {
	MANAGING_SYSTEM_STATE.strongAdaptations += 1;
	MANAGING_SYSTEM_STATE.performedAdaptations += 1;
};

const handleAddAdaptationLog = (msg) => {
	MANAGING_SYSTEM_STATE.adaptationLogs.push(msg.data);

	if (msg.data.agentName) {
		const { agentName } = msg.data;
		const node = getAgentNodeById(GRAPH_STATE.nodes, agentName);

		if (node) {
			node.adaptation = "active";
			finishAdaptation(node);
		}
	}
};

async function finishAdaptation(node) {
	await new Promise((f) => setTimeout(f, 1500));
	node.adaptation = "inactive";
}

export {
	handleRegisterGoals,
	handleUpdateIndicators,
	handleAddAdaptationLog,
	handleIncrementStrongAdaptations,
	handleIncrementWeakAdaptations,
	handleUpdateAdaptationAction,
};
