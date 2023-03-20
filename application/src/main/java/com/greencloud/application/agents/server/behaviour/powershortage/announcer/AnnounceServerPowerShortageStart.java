package com.greencloud.application.agents.server.behaviour.powershortage.announcer;

import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_START_DETECTED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_START_NO_IMPACT_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_START_TRANSFER_REQUEST_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToPowerShortageJob;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ALERT_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static com.greencloud.application.utils.AlgorithmUtils.findJobsWithinPower;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACTIVE_JOB_STATUSES;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import com.greencloud.application.agents.server.behaviour.powershortage.initiator.InitiateJobTransferInCloudNetwork;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.commons.domain.job.ClientJob;
import com.gui.event.domain.PowerShortageEvent;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour sends the information to the Cloud Network Agent that the power shortage has occurred
 */
public class AnnounceServerPowerShortageStart extends OneShotBehaviour {

	private static final Logger logger = getLogger(AnnounceServerPowerShortageStart.class);

	private final ServerAgent myServerAgent;
	private final Instant startTime;
	private final int newAvailablePower;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent       agent executing the behaviour
	 * @param powerShortage power shortage event that was detected
	 */
	public AnnounceServerPowerShortageStart(final ServerAgent myAgent, final PowerShortageEvent powerShortage) {
		super(myAgent);

		this.startTime = powerShortage.getOccurrenceTime();
		this.newAvailablePower = powerShortage.getNewMaximumCapacity();
		this.myServerAgent = myAgent;
	}

	/**
	 * Method is responsible for announcing to the cloud network that there will be some power shortage
	 * which cannot be handled by the server itself
	 */
	@Override
	public void action() {
		logger.info(POWER_SHORTAGE_START_DETECTED_LOG, startTime);
		final List<ClientJob> affectedJobs = getAffectedPowerJobs();

		if (affectedJobs.isEmpty()) {
			logger.info(POWER_SHORTAGE_START_NO_IMPACT_LOG);
			myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(emptyList(), startTime, myServerAgent,
					newAvailablePower));
			return;
		}

		final List<ClientJob> jobsToKeep = findJobsWithinPower(affectedJobs, newAvailablePower);
		final List<ClientJob> jobsTransfer = affectedJobs.stream().filter(job -> !jobsToKeep.contains(job)).toList();

		jobsTransfer.forEach(job -> {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(POWER_SHORTAGE_START_TRANSFER_REQUEST_LOG, job.getJobId());

			final ClientJob jobToTransfer = myServerAgent.manage().divideJobForPowerShortage(job, startTime);
			final JobPowerShortageTransfer originalJob = mapToPowerShortageJob(job, startTime);

			myServerAgent.addBehaviour(InitiateJobTransferInCloudNetwork.create(myServerAgent, originalJob,
					mapToPowerShortageJob(jobToTransfer, startTime), null));
			myServerAgent.send(prepareJobPowerShortageInformation(originalJob, SERVER_POWER_SHORTAGE_ALERT_PROTOCOL,
					myServerAgent.getGreenSourceForJobMap().get(job.getJobId())));
		});
		myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(jobsTransfer, startTime, myServerAgent,
				newAvailablePower));

	}

	private List<ClientJob> getAffectedPowerJobs() {
		return myServerAgent.getServerJobs().keySet().stream()
				.filter(job -> startTime.isBefore(job.getEndTime()))
				.filter(job -> ACTIVE_JOB_STATUSES.contains(myServerAgent.getServerJobs().get(job)))
				.toList();
	}
}
