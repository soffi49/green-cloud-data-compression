package com.greencloud.application.agents.server.behaviour.powershortage.announcer;

import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_FINISH_DETECTED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_FINISH_LEAVE_ON_HOLD_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_FINISH_UPDATE_CAPACITY_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_FINISH_UPDATE_JOB_STATUS_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_FINISH_USE_BACK_UP_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_FINISH_USE_GREEN_ENERGY_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.BACK_UP_POWER_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_BACK_UP;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_GREEN;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.utils.JobUtils;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour sends the information that the power shortage for given server has finished
 */
public class AnnounceServerPowerShortageFinish extends OneShotBehaviour {

	private static final Logger logger = getLogger(AnnounceServerPowerShortageFinish.class);

	private final ServerAgent myServerAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent agent executing the behaviour
	 */
	public AnnounceServerPowerShortageFinish(ServerAgent myAgent) {
		super(myAgent);
		this.myServerAgent = myAgent;
	}

	/**
	 * Method which is responsible for passing the information that the server power shortage has
	 * finished and that the jobs affected by it can be supplied using the green source power.
	 */
	@Override
	public void action() {
		logger.info(POWER_SHORTAGE_FINISH_DETECTED_LOG);
		myServerAgent.setCurrentMaximumCapacity(myServerAgent.getInitialMaximumCapacity());
		final List<ClientJob> affectedJobs = myServerAgent.manage().getActiveJobsOnHold(myServerAgent.getServerJobs());

		if (affectedJobs.isEmpty()) {
			logger.info(POWER_SHORTAGE_FINISH_UPDATE_CAPACITY_LOG);
		} else {
			logger.info(POWER_SHORTAGE_FINISH_UPDATE_JOB_STATUS_LOG);

			affectedJobs.forEach(job -> {
				final boolean isJobPresent = myServerAgent.getServerJobs().containsKey(job) &&
						myServerAgent.getGreenSourceForJobMap().containsKey(job.getJobId());

				if (isJobPresent) {
					handlePowerShortageFinish(job);
				}
			});
		}
	}

	private void handlePowerShortageFinish(final ClientJob job) {
		final JobInstanceIdentifier jobInstance = mapToJobInstanceId(job);
		final int jobPower = job.getPower();
		final int availablePower = myServerAgent.manage().getAvailableCapacity(job, jobInstance, null);
		final int backUpPower = myServerAgent.manage()
				.getAvailableCapacity(job, jobInstance, EXECUTING_ON_BACK_UP.getStatuses());

		MDC.put(MDC_JOB_ID, job.getJobId());
		if (availablePower < jobPower && backUpPower < jobPower) {
			logger.info(POWER_SHORTAGE_FINISH_LEAVE_ON_HOLD_LOG, job.getJobId());
		} else if (backUpPower >= jobPower) {
			logger.info(POWER_SHORTAGE_FINISH_USE_BACK_UP_LOG, job.getJobId());
			supplyJobWithBackUpPower(job);
		} else {
			logger.info(POWER_SHORTAGE_FINISH_USE_GREEN_ENERGY_LOG, job.getJobId());
			supplyJobWithGreenEnergy(job, jobInstance);
		}
	}

	private void supplyJobWithBackUpPower(final ClientJob job) {
		final boolean hasStarted = myServerAgent.getServerJobs().get(job).equals(ON_HOLD);

		myServerAgent.getServerJobs().replace(job, EXECUTING_ON_BACK_UP.getStatus(hasStarted));
		myServerAgent.manage().updateGUI();

		if (hasStarted) {
			myServerAgent.message().informCNAAboutStatusChange(mapToJobInstanceId(job), BACK_UP_POWER_JOB_ID);
		}
	}

	private void supplyJobWithGreenEnergy(final ClientJob job, final JobInstanceIdentifier jobInstance) {
		final boolean hasStarted = JobUtils.isJobStarted(job, myServerAgent.getServerJobs());

		myServerAgent.getServerJobs().replace(job, EXECUTING_ON_GREEN.getStatus(hasStarted));

		if (hasStarted) {
			myServerAgent.message().informCNAAboutStatusChange(mapToJobInstanceId(job), GREEN_POWER_JOB_ID);
		}

		myServerAgent.manage().updateGUI();
		myServerAgent.send(prepareJobPowerShortageInformation(jobInstance, POWER_SHORTAGE_FINISH_ALERT_PROTOCOL,
				myServerAgent.getGreenSourceForJobMap().get(job.getJobId()),
				myServerAgent.getOwnerCloudNetworkAgent()));
	}
}
