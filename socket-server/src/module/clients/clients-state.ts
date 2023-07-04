import { AGENT_TYPES, JOB_STATUSES } from "../../constants/constants";
import { AgentEvent, ClientStatusReportEntry, ReportEntry } from "../../types";

interface JobDurationMap {
	[key: string]: number;
	CREATED: number;
	PROCESSED: number;
	IN_PROGRESS: number;
	DELAYED: number;
	FINISHED: number;
	ON_BACK_UP: number;
	ON_HOLD: number;
	REJECTED: number;
	FAILED: number;
}

interface Job {
	jobId: string;
	power: string;
	start: string;
	end: string;
	deadline: string;
}

interface SplitJob {
	splitJobId: string;
	power: string;
	start: number;
	end: number;
	status: JOB_STATUSES;
}

interface Client {
	type: AGENT_TYPES;
	name: string;
	events: AgentEvent[];
	isActive: boolean;
	adaptation: string;
	job: Job;
	status: JOB_STATUSES;
	isSplit: boolean;
	splitJobs: SplitJob[];
	durationMap: JobDurationMap | null;
	jobExecutionProportion: number;
}

interface ClientState {
	clients: Client[];
}

interface ClientsReportsState {
	executedJobsReport: ReportEntry[];
	avgJobSizeReport: ReportEntry[];
	minJobSizeReport: ReportEntry[];
	maxJobSizeReport: ReportEntry[];
	jobsExecutedAsWhole: ReportEntry[];
	jobsExecutedInParts: ReportEntry[];
	clientsStatusReport: ClientStatusReportEntry[];
	avgClientsExecutionPercentage: ReportEntry[];
	minClientsExecutionPercentage: ReportEntry[];
	maxClientsExecutionPercentage: ReportEntry[];
}

let CLIENTS_STATE: ClientState = {
	clients: [],
};

let CLIENTS_REPORTS_STATE: ClientsReportsState = {
	executedJobsReport: [],
	avgJobSizeReport: [],
	minJobSizeReport: [],
	maxJobSizeReport: [],
	clientsStatusReport: [],
	jobsExecutedAsWhole: [],
	jobsExecutedInParts: [],
	avgClientsExecutionPercentage: [],
	minClientsExecutionPercentage: [],
	maxClientsExecutionPercentage: [],
};

const resetClientsState = () =>
	Object.assign(CLIENTS_STATE, {
		clients: [],
	});

const resetClientsReportsState = () =>
	Object.assign(CLIENTS_REPORTS_STATE, {
		executedJobsReport: [],
		avgJobSizeReport: [],
		minJobSizeReport: [],
		maxJobSizeReport: [],
		clientsStatusReport: [],
		jobsExecutedAsWhole: [],
		jobsExecutedInParts: [],
		avgClientsExecutionPercentage: [],
		minClientsExecutionPercentage: [],
		maxClientsExecutionPercentage: [],
	});

export {
	CLIENTS_STATE,
	CLIENTS_REPORTS_STATE,
	Client,
	ClientsReportsState,
	resetClientsState,
	resetClientsReportsState,
};
