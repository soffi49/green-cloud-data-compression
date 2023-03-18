package com.greencloud.application.agents.cloudnetwork.management;

import static com.database.knowledge.domain.agent.DataType.CLOUD_NETWORK_MONITORING;
import static com.greencloud.application.agents.cloudnetwork.constants.CloudNetworkAgentConstants.MAX_POWER_DIFFERENCE;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_ACCEPTED_LOG;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_FINISH_LOG;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_PROCESS_LOG;
import static com.greencloud.application.agents.cloudnetwork.management.logs.CloudNetworkManagementLog.COUNT_JOB_START_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.GUIUtils.announceFinishedJob;
import static com.greencloud.application.utils.JobUtils.getJobSuccessRatio;
import static com.greencloud.application.utils.StateManagementUtils.getCurrentPowerInUse;
import static com.greencloud.application.utils.StateManagementUtils.getPowerPercent;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;
import static java.util.Arrays.stream;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toConcurrentMap;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.database.knowledge.domain.agent.cloudnetwork.CloudNetworkMonitoringData;
import com.database.knowledge.domain.agent.cloudnetwork.ImmutableCloudNetworkMonitoringData;
import com.greencloud.application.agents.AbstractAgentManagement;
import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.domain.agent.ServerData;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;
import com.gui.agents.CloudNetworkAgentNode;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Class stores methods used to manage the state of Cloud Network Agent
 */
public class CloudNetworkStateManagement extends AbstractAgentManagement {

	private static final Logger logger = LoggerFactory.getLogger(CloudNetworkStateManagement.class);

	protected final CloudNetworkAgent cloudNetworkAgent;
	protected final ConcurrentMap<JobExecutionResultEnum, Long> jobCounters;

	public CloudNetworkStateManagement(CloudNetworkAgent cloudNetworkAgent) {
		this.cloudNetworkAgent = cloudNetworkAgent;
		this.jobCounters = stream(JobExecutionResultEnum.values()).collect(toConcurrentMap(key -> key, status -> 0L));
	}

	/**
	 * Method retrieves list of owned servers that are active
	 *
	 * @return list of server AIDs
	 */
	public List<AID> getOwnedActiveServers() {
		return cloudNetworkAgent.getOwnedServers().entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toList();
	}

	/**
	 * Method compares offers for job execution proposed by 2 servers
	 *
	 * @param offer1 first offer used in comparison
	 * @param offer2 second offer used in comparison
	 * @return method returns:
	 * <p> val > 0 - if the offer1 is better</p>
	 * <p> val = 0 - if both offers are equivalently good</p>
	 * <p> val < 0 - if the offer2 is better</p>
	 */
	public int compareServerOffers(final ACLMessage offer1, final ACLMessage offer2) {
		final int weight1 = cloudNetworkAgent.getWeightsForServersMap().get(offer1.getSender());
		final int weight2 = cloudNetworkAgent.getWeightsForServersMap().get(offer2.getSender());

		final Comparator<ServerData> comparator = (server1Data, server2Data) -> {
			final int powerDifference =
					(server2Data.getAvailablePower() * weight2) - (server1Data.getAvailablePower() * weight1);
			final double priceDifference =
					((server1Data.getServicePrice() * 1 / weight1) - (server2Data.getServicePrice() * 1 / weight2));

			return MAX_POWER_DIFFERENCE.isValidIntValue(powerDifference) ? (int) priceDifference : powerDifference;
		};

		return compareReceivedOffers(offer1, offer2, ServerData.class, comparator);
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
				logger.info(COUNT_JOB_START_LOG, jobId, jobCounters.get(STARTED), jobCounters.get(ACCEPTED));
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
	 * Method updates Cloud Network statistics in GUI
	 */
	public void updateCloudNetworkGUI() {
		final CloudNetworkAgentNode cloudNetworkAgentNode = (CloudNetworkAgentNode) cloudNetworkAgent.getAgentNode();

		if (nonNull(cloudNetworkAgentNode)) {
			final double successRatio = getJobSuccessRatio(jobCounters.get(ACCEPTED), jobCounters.get(FAILED));
			cloudNetworkAgentNode.updateClientNumber(getScheduledJobs());
			cloudNetworkAgentNode.updateJobsCount(getJobInProgressCount());
			cloudNetworkAgentNode.updateTraffic(getCurrentPowerInUse(cloudNetworkAgent.getNetworkJobs()));
			cloudNetworkAgentNode.updateCurrentJobSuccessRatio(successRatio);
		}
		cloudNetworkAgent.manage().saveMonitoringData();
	}

	/**
	 * Method assembles the object that stores Cloud Network monitoring data and saves it in the database
	 */
	public void saveMonitoringData() {
		final int maxCapacity = (int) cloudNetworkAgent.getMaximumCapacity();
		final int traffic = getCurrentPowerInUse(cloudNetworkAgent.getNetworkJobs());
		final CloudNetworkMonitoringData cloudNetworkMonitoringData = ImmutableCloudNetworkMonitoringData.builder()
				.currentTraffic(getPowerPercent(traffic, maxCapacity))
				.availablePower((double) maxCapacity - traffic)
				.successRatio(getJobSuccessRatio(jobCounters.get(ACCEPTED), jobCounters.get(FAILED)))
				.build();
		cloudNetworkAgent.writeMonitoringData(CLOUD_NETWORK_MONITORING, cloudNetworkMonitoringData);
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
}
