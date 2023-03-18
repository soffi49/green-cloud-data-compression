package com.greencloud.application.agents.cloudnetwork.domain;

import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.JOB_CONFIRMED_STATUS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.SEND_BACK_UP_STATUS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.SEND_GREEN_POWER_STATUS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.SEND_JOB_FAILED_STATUS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.SEND_JOB_FINISH_STATUS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.SEND_JOB_START_STATUS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.SEND_ON_HOLD_STATUS_LOG;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;

import java.util.function.BiConsumer;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.handler.HandleDelayedJob;
import com.greencloud.commons.domain.job.ClientJob;

/**
 * Class containing data associated with processing by the Cloud Network the updates
 * regarding the execution of the client's job
 */
public enum CloudNetworkJobUpdateEnum {

	FAILED_JOB_ID(SEND_JOB_FAILED_STATUS_LOG, processJobFail(), true),
	CONFIRMED_JOB_ID(JOB_CONFIRMED_STATUS_LOG, processJobConfirmation(), false),
	STARTED_JOB_ID(SEND_JOB_START_STATUS_LOG, processJobStart(), true),
	FINISH_JOB_ID(SEND_JOB_FINISH_STATUS_LOG, processJobFinish(), true),
	ON_HOLD_JOB_ID(SEND_ON_HOLD_STATUS_LOG, null, true),
	GREEN_POWER_JOB_ID(SEND_GREEN_POWER_STATUS_LOG, null, true),
	BACK_UP_POWER_JOB_ID(SEND_BACK_UP_STATUS_LOG, null, true);

	private final String logMessage;
	private final BiConsumer<ClientJob, CloudNetworkAgent> jobUpdateHandler;
	private final boolean informScheduler;

	CloudNetworkJobUpdateEnum(final String logMessage, final BiConsumer<ClientJob, CloudNetworkAgent> jobUpdateHandler,
			final boolean informScheduler) {
		this.logMessage = logMessage;
		this.jobUpdateHandler = jobUpdateHandler;
		this.informScheduler = informScheduler;
	}

	/**
	 * @return method used in processing the information that job execution has been confirmed
	 */
	private static BiConsumer<ClientJob, CloudNetworkAgent> processJobConfirmation() {
		return (job, myCloudNetworkAgent) -> {
			myCloudNetworkAgent.getNetworkJobs().replace(job, ACCEPTED);
			myCloudNetworkAgent.addBehaviour(HandleDelayedJob.create(myCloudNetworkAgent, job));
		};
	}

	/**
	 * @return method used in processing the information that job execution has started
	 */
	private static BiConsumer<ClientJob, CloudNetworkAgent> processJobStart() {
		return (job, myCloudNetworkAgent) -> {
			if (!myCloudNetworkAgent.getNetworkJobs().get(job).equals(IN_PROGRESS)) {
				myCloudNetworkAgent.getNetworkJobs().replace(job, IN_PROGRESS);
				myCloudNetworkAgent.manage().incrementJobCounter(job.getJobId(), STARTED);
			}
		};
	}

	/**
	 * @return method used in processing the information that job execution has finished
	 */
	private static BiConsumer<ClientJob, CloudNetworkAgent> processJobFinish() {
		return (job, myCloudNetworkAgent) -> {
			if (isJobStarted(job, myCloudNetworkAgent.getNetworkJobs())) {
				myCloudNetworkAgent.manage().incrementJobCounter(job.getJobId(), FINISH);
			}
			myCloudNetworkAgent.getNetworkJobs().remove(job);
			myCloudNetworkAgent.getServerForJobMap().remove(job.getJobId());
			myCloudNetworkAgent.manage().updateCloudNetworkGUI();
		};
	}

	/**
	 * @return method used in processing the information that job execution failed
	 */
	private static BiConsumer<ClientJob, CloudNetworkAgent> processJobFail() {
		return (job, myCloudNetworkAgent) -> {
			if (!myCloudNetworkAgent.getNetworkJobs().get(job).equals(PROCESSING)) {
				myCloudNetworkAgent.getGuiController().updateAllJobsCountByValue(-1);
			}
			myCloudNetworkAgent.getNetworkJobs().remove(job);
			myCloudNetworkAgent.getServerForJobMap().remove(job.getJobId());
			myCloudNetworkAgent.manage().incrementJobCounter(job.getJobId(), FAILED);
		};
	}

	public String getLogMessage() {
		return logMessage;
	}

	public BiConsumer<ClientJob, CloudNetworkAgent> getJobUpdateHandler() {
		return jobUpdateHandler;
	}

	public boolean isInformScheduler() {
		return informScheduler;
	}
}
