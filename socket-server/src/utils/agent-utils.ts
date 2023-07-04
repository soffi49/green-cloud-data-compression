import { INITIAL_NETWORK_AGENT_STATE, INITIAL_POWER_SHORTAGE_STATE, JOB_STATUSES } from "../constants/constants";
import { AGENT_TYPES } from "../constants/constants";
import {
	AGENTS_REPORTS_STATE,
	changeCloudNetworkCapacityEvent,
	addGreenSourcesToServer,
	addServersToCNA,
	Client,
} from "../module";

const getAgentByName = (agents: any[], agentName: string) => {
	return agents.find((agent) => agent.name === agentName);
};

const getAgentNodeById = (nodes: any[], id: string) => {
	return nodes.find((node) => node.id === id);
};

const registerClient = (data): Client => {
	const { name, ...jobData } = data;

	return {
		type: AGENT_TYPES.CLIENT,
		status: JOB_STATUSES.CREATED,
		events: [],
		name,
		isActive: false,
		adaptation: "inactive",
		isSplit: false,
		splitJobs: [],
		durationMap: null,
		job: jobData,
		jobExecutionProportion: 0,
	};
};

const registerScheduler = (data) => {
	AGENTS_REPORTS_STATE.agentsReports.push({
		name: data.name,
		type: AGENT_TYPES.SCHEDULER,
		reports: {
			deadlinePriorityReport: [],
			powerPriorityReport: [],
			clientRequestReport: [],
			queueCapacityReport: [],
			trafficReport: [],
		},
		events: [],
	});
	return {
		type: AGENT_TYPES.SCHEDULER,
		scheduledJobs: [],
		events: [],
		isActive: true,
		adaptation: "inactive",
		...data,
	};
};

const registerCloudNetwork = (data) => {
	AGENTS_REPORTS_STATE.agentsReports.push({
		name: data.name,
		type: AGENT_TYPES.CLOUD_NETWORK,
		reports: {
			clientsReport: [],
			capacityReport: [],
			trafficReport: [],
			successRatioReport: [],
		},
		events: [],
	});

	return {
		type: AGENT_TYPES.CLOUD_NETWORK,
		traffic: 0,
		totalNumberOfClients: 0,
		totalNumberOfExecutedJobs: 0,
		events: [],
		isActive: false,
		adaptation: "inactive",
		...data,
	};
};

const registerGreenEnergy = (data) => {
	const events = [structuredClone(INITIAL_POWER_SHORTAGE_STATE)];

	AGENTS_REPORTS_STATE.agentsReports.push({
		name: data.name,
		type: AGENT_TYPES.GREEN_ENERGY,
		reports: {
			trafficReport: [],
			availableGreenPowerReport: [],
			capacityReport: [],
			jobsOnGreenPowerReport: [],
			jobsOnHoldReport: [],
			successRatioReport: [],
		},
		events: [],
	});

	addGreenSourcesToServer(data);

	return {
		type: AGENT_TYPES.GREEN_ENERGY,
		events,
		isActive: false,
		adaptation: "inactive",
		availableGreenEnergy: 0,
		connectedServers: [data.serverAgent],
		...INITIAL_NETWORK_AGENT_STATE(data),
		...data,
	};
};

const registerServer = (data) => {
	const events = [structuredClone(INITIAL_POWER_SHORTAGE_STATE)];

	AGENTS_REPORTS_STATE.agentsReports.push({
		name: data.name,
		type: AGENT_TYPES.SERVER,
		reports: {
			trafficReport: [],
			capacityReport: [],
			successRatioReport: [],
			greenPowerUsageReport: [],
			backUpPowerUsageReport: [],
		},
		events: [],
	});

	addServersToCNA(data);
	changeCloudNetworkCapacityEvent(data.cloudNetworkAgent, data.name, data.initialMaximumCapacity, true, true);

	return {
		type: AGENT_TYPES.SERVER,
		totalNumberOfClients: 0,
		backUpTraffic: 0,
		events,
		isActive: false,
		adaptation: "inactive",
		...INITIAL_NETWORK_AGENT_STATE(data),
		...data,
	};
};

const registerMonitoring = (data) => {
	return {
		type: AGENT_TYPES.MONITORING,
		events: [],
		isActive: false,
		adaptation: "inactive",
		...data,
	};
};

const registerAgent = (data, type) => {
	switch (type) {
		case AGENT_TYPES.CLIENT:
			return registerClient(data);
		case AGENT_TYPES.CLOUD_NETWORK:
			return registerCloudNetwork(data);
		case AGENT_TYPES.GREEN_ENERGY:
			return registerGreenEnergy(data);
		case AGENT_TYPES.MONITORING:
			return registerMonitoring(data);
		case AGENT_TYPES.SERVER:
			return registerServer(data);
		case AGENT_TYPES.SCHEDULER:
			return registerScheduler(data);
	}
};

export {
	getAgentByName,
	getAgentNodeById,
	registerClient,
	registerScheduler,
	registerCloudNetwork,
	registerGreenEnergy,
	registerServer,
	registerMonitoring,
	registerAgent,
};
