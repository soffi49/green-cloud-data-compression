package com.greencloud.application.agents.cloudnetwork.management;

import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_ACCEPTED_LOG;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_FINISH_LOG;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_PROCESS_LOG;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_START_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.GUIUtils.announceFinishedJob;
import static com.greencloud.application.utils.JobUtils.getJobSuccessRatio;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static java.util.Objects.nonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;
import com.gui.agents.CloudNetworkAgentNode;

/**
 * Set of utilities used to manage the state of the cloud network
 */
public class CloudNetworkStateManagement {

	private static final Logger logger = LoggerFactory.getLogger(CloudNetworkStateManagement.class);
	private final CloudNetworkAgent cloudNetworkAgent;
	private final ConcurrentMap<JobExecutionResultEnum, Long> jobCounters;

	public CloudNetworkStateManagement(CloudNetworkAgent cloudNetworkAgent) {
		this.cloudNetworkAgent = cloudNetworkAgent;
		this.jobCounters = Arrays.stream(JobExecutionResultEnum.values())
				.collect(Collectors.toConcurrentMap(result -> result, status -> 0L));
	}

	/**
	 * Method calculates the power in use at the given moment
	 *
	 * @return current power in use
	 */
	public int getCurrentPowerInUse() {
		return cloudNetworkAgent.getNetworkJobs().entrySet()
				.stream()
				.filter(job -> job.getValue().equals(IN_PROGRESS))
				.mapToInt(job -> job.getKey().getPower())
				.sum();
	}

	/**
	 * Method increments the counter of jobs
	 *
	 * @param jobId job identifier
	 * @param type  type of counter to increment
	 */
	public void incrementJobCounter(final String jobId, final JobExecutionResultEnum type) {
		MDC.put(MDC_JOB_ID, jobId);
		jobCounters.computeIfPresent(type, (key, val) -> val += 1);

		switch (type) {
			case FAILED -> logger.info(COUNT_JOB_PROCESS_LOG, jobCounters.get(FAILED));
			case ACCEPTED -> logger.info(COUNT_JOB_ACCEPTED_LOG, jobCounters.get(ACCEPTED));
			case STARTED -> {
				logger.info(COUNT_JOB_START_LOG, jobId, jobCounters.get(STARTED),
						jobCounters.get(ACCEPTED));
				cloudNetworkAgent.getGuiController().updateActiveJobsCountByValue(1);
			}
			case FINISH -> {
				logger.info(COUNT_JOB_FINISH_LOG, jobId, jobCounters.get(FINISH), jobCounters.get(STARTED));
				announceFinishedJob(cloudNetworkAgent);
			}
		}
		updateCloudNetworkGUI();
	}

	/**
	 * Method updates information regarding CNA's maximum capacity
	 *
	 * @param newCapacity new maximum capacity
	 */
	public void updateMaximumCapacity(final double newCapacity) {
		cloudNetworkAgent.setMaximumCapacity(newCapacity);
		((CloudNetworkAgentNode) cloudNetworkAgent.getAgentNode()).updateMaximumCapacity(newCapacity,
				getCurrentPowerInUse());
	}

	public Map<JobExecutionResultEnum, Long> getJobCounters() {
		return jobCounters;
	}

	/**
	 * Method updates Cloud Network statistics in GUI
	 */
	public void updateCloudNetworkGUI() {
		final CloudNetworkAgentNode cloudNetworkAgentNode = (CloudNetworkAgentNode) cloudNetworkAgent.getAgentNode();

		if (nonNull(cloudNetworkAgentNode)) {
			final double successRatio = getJobSuccessRatio(cloudNetworkAgent.manage().getJobCounters().get(ACCEPTED),
					cloudNetworkAgent.manage().getJobCounters().get(FAILED));
			cloudNetworkAgentNode.updateClientNumber(getScheduledJobs());
			cloudNetworkAgentNode.updateJobsCount(getJobInProgressCount());
			cloudNetworkAgentNode.updateTraffic(getCurrentPowerInUse());
			cloudNetworkAgentNode.updateCurrentJobSuccessRatio(successRatio);
		}
		cloudNetworkAgent.manageConfig().saveMonitoringData();
	}

	private int getJobInProgressCount() {
		return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(IN_PROGRESS))
				.toList()
				.size();
	}

	private int getScheduledJobs() {
		return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
				.filter(job -> !job.getValue().equals(PROCESSING))
				.toList()
				.size();
	}

	/**
	 * Method returns the success ratio of the cna
	 *
	 * @return success ratio
	 */
	public double getSuccessRatio() {
		return getJobSuccessRatio(jobCounters.get(ACCEPTED), jobCounters.get(FAILED));
	}
}
