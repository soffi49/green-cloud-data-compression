package com.greencloud.application.agents.greenenergy.management;

import static com.database.knowledge.domain.agent.DataType.GREEN_SOURCE_MONITORING;
import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.POWER_JOB_ACCEPTED_LOG;
import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.POWER_JOB_FAILED_LOG;
import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.POWER_JOB_FINISH_LOG;
import static com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog.POWER_JOB_START_LOG;
import static com.greencloud.application.utils.JobUtils.divideJobForTransfer;
import static com.greencloud.application.utils.JobUtils.getJobCount;
import static com.greencloud.application.utils.JobUtils.getJobSuccessRatio;
import static com.greencloud.application.utils.StateManagementUtils.getCurrentPowerInUse;
import static com.greencloud.application.utils.StateManagementUtils.getPowerPercent;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.JOB_ON_HOLD_STATUSES;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import org.slf4j.Logger;

import com.database.knowledge.domain.agent.greensource.GreenSourceMonitoringData;
import com.database.knowledge.domain.agent.greensource.ImmutableGreenSourceMonitoringData;
import com.greencloud.application.agents.AbstractStateManagement;
import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.behaviour.adaptation.InitiateGreenSourceDisconnection;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.handler.HandleManualPowerSupplyFinish;
import com.greencloud.application.domain.job.JobCounter;
import com.greencloud.commons.domain.job.ServerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;
import com.gui.agents.GreenEnergyAgentNode;

import jade.core.AID;

/**
 * Set of methods used to manage the internal state of the green energy agent
 */
public class GreenEnergyStateManagement extends AbstractStateManagement {

	private static final Logger logger = getLogger(GreenEnergyStateManagement.class);

	private final AtomicInteger shortagesAccumulator;
	private final AtomicInteger weatherShortagesCounter;
	private final GreenEnergyAgent greenEnergyAgent;

	/**
	 * Default constructor
	 *
	 * @param greenEnergyAgent - agent representing given source
	 */
	public GreenEnergyStateManagement(GreenEnergyAgent greenEnergyAgent) {
		this.greenEnergyAgent = greenEnergyAgent;
		this.shortagesAccumulator = new AtomicInteger(0);
		this.weatherShortagesCounter = new AtomicInteger(0);
	}

	/**
	 * Method creates new instances for given server job that will be affected by the power shortage and defines
	 * the post job division handler.
	 */
	public ServerJob divideServerJobForPowerShortage(final ServerJob serverJob, final Instant powerShortageStart) {
		final BiConsumer<ServerJob, ServerJob> jobDivisionHandler = (affectedJob, nonAffectedJob) -> {
			incrementJobCounter(affectedJob.getJobId(), ACCEPTED);
			greenEnergyAgent.addBehaviour(HandleManualPowerSupplyFinish.create(greenEnergyAgent, nonAffectedJob));
		};

		final ServerJob jobToTransfer = divideJobForTransfer(serverJob, powerShortageStart,
				greenEnergyAgent.getServerJobs(), jobDivisionHandler);
		updateGUI();
		return jobToTransfer;
	}

	/**
	 * Method removes a job from Green Source map.
	 * Then it performs post-removal actions that verify if the given Green Source is undergoing disconnection, and
	 * if so - checks if the Green Source can be fully disconnected
	 *
	 * @param job job to be removed
	 */
	public void removeJob(final ServerJob job) {
		greenEnergyAgent.getServerJobs().remove(job);

		if (greenEnergyAgent.adapt().getDisconnectionState().isBeingDisconnectedFromServer()) {
			final AID server = greenEnergyAgent.adapt().getDisconnectionState().getServerToBeDisconnected();
			final boolean isLastJobRemoved = greenEnergyAgent.getServerJobs().keySet().stream()
					.noneMatch(serverJob -> serverJob.getServer().equals(server));

			if (isLastJobRemoved) {
				greenEnergyAgent.addBehaviour(InitiateGreenSourceDisconnection.create(greenEnergyAgent, server));
			}
		}
	}

	@Override
	protected ConcurrentMap<JobExecutionResultEnum, JobCounter> getJobCountersMap() {
		return new ConcurrentHashMap<>(Map.of(
				FAILED, new JobCounter(jobId ->
						logger.info(POWER_JOB_FAILED_LOG, jobCounters.get(FAILED).getCount())),
				ACCEPTED, new JobCounter(jobId ->
						logger.info(POWER_JOB_ACCEPTED_LOG, jobCounters.get(ACCEPTED).getCount())),
				STARTED, new JobCounter(jobId ->
						logger.info(POWER_JOB_START_LOG, jobId, jobCounters.get(STARTED).getCount(),
								jobCounters.get(ACCEPTED).getCount())),
				FINISH, new JobCounter(jobId ->
						logger.info(POWER_JOB_FINISH_LOG, jobId, jobCounters.get(FINISH).getCount(),
								jobCounters.get(STARTED).getCount()))
		));
	}

	@Override
	public void updateGUI() {
		final GreenEnergyAgentNode greenEnergyAgentNode = (GreenEnergyAgentNode) greenEnergyAgent.getAgentNode();

		if (nonNull(greenEnergyAgentNode)) {
			final double successRatio = getJobSuccessRatio(jobCounters.get(ACCEPTED).getCount(),
					jobCounters.get(FAILED).getCount());
			final int powerInUse = getCurrentPowerInUse(greenEnergyAgent.getServerJobs());

			greenEnergyAgentNode.updateMaximumCapacity(greenEnergyAgent.getCurrentMaximumCapacity(), powerInUse);
			greenEnergyAgentNode.updateJobsCount(getJobCount(greenEnergyAgent.getServerJobs()));
			greenEnergyAgentNode.updateJobsOnHoldCount(
					getJobCount(greenEnergyAgent.getServerJobs(), JOB_ON_HOLD_STATUSES));
			greenEnergyAgentNode.updateIsActive(getIsActiveState());
			greenEnergyAgentNode.updateTraffic(powerInUse);
			greenEnergyAgentNode.updateCurrentJobSuccessRatio(successRatio);
			saveMonitoringData();
		}
	}

	public AtomicInteger getWeatherShortagesCounter() {
		return weatherShortagesCounter;
	}

	public AtomicInteger getShortagesAccumulator() {
		return shortagesAccumulator;
	}

	private void saveMonitoringData() {
		final double trafficOverall = getPowerPercent(getCurrentPowerInUse(greenEnergyAgent.getServerJobs()),
				greenEnergyAgent.getCurrentMaximumCapacity());

		final GreenSourceMonitoringData greenSourceMonitoring = ImmutableGreenSourceMonitoringData.builder()
				.currentTraffic(trafficOverall)
				.weatherPredictionError(greenEnergyAgent.getWeatherPredictionError())
				.successRatio(
						getJobSuccessRatio(jobCounters.get(ACCEPTED).getCount(), jobCounters.get(FAILED).getCount()))
				.isBeingDisconnected(greenEnergyAgent.adapt().getDisconnectionState().isBeingDisconnected())
				.build();
		greenEnergyAgent.writeMonitoringData(GREEN_SOURCE_MONITORING, greenSourceMonitoring);
	}

	private boolean getIsActiveState() {
		return getCurrentPowerInUse(greenEnergyAgent.getServerJobs()) > 0
				|| getJobCount(greenEnergyAgent.getServerJobs(), JOB_ON_HOLD_STATUSES) > 0;
	}
}
