import { JOB_STATUSES } from "../../constants/constants";
import { CLIENTS_REPORTS_STATE, CLIENTS_STATE } from "./clients-state";

const reportExecutedJob = (time: number) => {
	const activeStatuses = [JOB_STATUSES.IN_PROGRESS, JOB_STATUSES.ON_BACK_UP, JOB_STATUSES.ON_HOLD];
	const jobsNo = CLIENTS_STATE.clients.filter((client) => activeStatuses.includes(client.status)).length;

	return { time, value: jobsNo };
};

const reportJobSizeData = (time: number) => {
	const activeStatuses = [JOB_STATUSES.IN_PROGRESS, JOB_STATUSES.ON_BACK_UP, JOB_STATUSES.ON_HOLD];
	const jobSizes = CLIENTS_STATE.clients
		.filter((client) => activeStatuses.includes(client.status))
		.map((client) => parseInt(client.job.power));
	const isEmpty = jobSizes.length === 0;
	const avg = !isEmpty ? jobSizes.reduce((size1, size2) => size1 + size2, 0) / jobSizes.length : 0;

	return { time, avg, min: isEmpty ? 0 : Math.min(...jobSizes), max: isEmpty ? 0 : Math.max(...jobSizes) };
};

const reportJobStatusExecutionTime = () => {
	const clientsNo = CLIENTS_STATE.clients.length;
	const clientsDurationMaps = CLIENTS_STATE.clients.map((client) => client.durationMap);

	return Object.keys(JOB_STATUSES).map((status) => {
		const value =
			clientsDurationMaps
				?.map((durationMap) => (durationMap === null ? 0 : durationMap[status]))
				.reduce((prev, curr) => prev + curr, 0) ?? 0;
		return { status, value: clientsNo !== 0 ? value / clientsNo : 0 };
	});
};

const reportProportionOfExecutedJobs = () => {
	const finishedJobs = CLIENTS_STATE.clients.filter((client) =>
		[JOB_STATUSES.FAILED, JOB_STATUSES.FINISHED].includes(client.status)
	);
	const jobsExecutedAsWhole = finishedJobs.filter((job) => job.isSplit === false).length;
	const jobsExecutedInParts = finishedJobs.filter((job) => job.isSplit === true).length;

	return { whole: jobsExecutedAsWhole, parts: jobsExecutedInParts };
};

const reportJobExecutionPercentages = () => {
	const clientsNo = CLIENTS_STATE.clients.length;
	const clientsPercentages = CLIENTS_STATE.clients
		.filter((client) => client.status === JOB_STATUSES.FINISHED)
		.map((client) => client.jobExecutionProportion);

	const clientPercentageSum = clientsPercentages.reduce((prev, curr) => prev + curr, 0);
	const avgPercentage = (clientsNo !== 0 ? clientPercentageSum / clientsNo : 0) * 100;
	const minPercentage = Math.min(...clientsPercentages) * 100;
	const maxPercentage = Math.max(...clientsPercentages) * 100;

	return { avgPercentage, minPercentage, maxPercentage };
};

const updateClientReportsState = (time) => {
	const jobSizeData = reportJobSizeData(time);
	const jobExecutionProportion = reportProportionOfExecutedJobs();
	const jobPercentages = reportJobExecutionPercentages();

	const executedJobsReport = CLIENTS_REPORTS_STATE.executedJobsReport.concat(reportExecutedJob(time));
	const avgJobSizeReport = CLIENTS_REPORTS_STATE.avgJobSizeReport.concat({
		time: jobSizeData.time,
		value: jobSizeData.avg,
	});
	const minJobSizeReport = CLIENTS_REPORTS_STATE.minJobSizeReport.concat({
		time: jobSizeData.time,
		value: jobSizeData.min,
	});
	const maxJobSizeReport = CLIENTS_REPORTS_STATE.maxJobSizeReport.concat({
		time: jobSizeData.time,
		value: jobSizeData.max,
	});
	const clientsStatusReport = CLIENTS_REPORTS_STATE.clientsStatusReport.concat({
		time,
		value: reportJobStatusExecutionTime(),
	});
	const jobsExecutedAsWhole = CLIENTS_REPORTS_STATE.jobsExecutedAsWhole.concat({
		time,
		value: jobExecutionProportion.whole,
	});
	const jobsExecutedInParts = CLIENTS_REPORTS_STATE.jobsExecutedInParts.concat({
		time,
		value: jobExecutionProportion.parts,
	});
	const avgClientsExecutionPercentage = CLIENTS_REPORTS_STATE.avgClientsExecutionPercentage.concat({
		time,
		value: jobPercentages.avgPercentage,
	});
	const minClientsExecutionPercentage = CLIENTS_REPORTS_STATE.minClientsExecutionPercentage.concat({
		time,
		value: jobPercentages.minPercentage,
	});
	const maxClientsExecutionPercentage = CLIENTS_REPORTS_STATE.maxClientsExecutionPercentage.concat({
		time,
		value: jobPercentages.maxPercentage,
	});

	Object.assign(CLIENTS_REPORTS_STATE, {
		executedJobsReport,
		avgJobSizeReport,
		minJobSizeReport,
		maxJobSizeReport,
		clientsStatusReport,
		jobsExecutedAsWhole,
		jobsExecutedInParts,
		avgClientsExecutionPercentage,
		minClientsExecutionPercentage,
		maxClientsExecutionPercentage,
	});
};

export { updateClientReportsState };
