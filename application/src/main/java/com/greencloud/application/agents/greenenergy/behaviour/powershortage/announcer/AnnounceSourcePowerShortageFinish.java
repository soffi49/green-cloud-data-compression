package com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer;

import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.CHANGE_JOB_STATUS_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.NO_POWER_LEAVE_ON_HOLD_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.POWER_SHORTAGE_SOURCE_FINISH_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.POWER_SHORTAGE_SOURCE_FINISH_NO_JOBS_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.POWER_SHORTAGE_SOURCE_JOB_ENDED_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.POWER_SHORTAGE_SOURCE_VERIFY_POWER_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.announcer.logs.PowerShortageSourceAnnouncerLog.WEATHER_UNAVAILABLE_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.RequestWeatherData.createWeatherRequest;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.ON_HOLD_JOB_CHECK_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_PLANNED;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.domain.job.ServerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour announces that the power shortage has finished at the given moment
 */
public class AnnounceSourcePowerShortageFinish extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(AnnounceSourcePowerShortageFinish.class);

	private final GreenEnergyAgent myGreenAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent agent executing the behaviour
	 */
	public AnnounceSourcePowerShortageFinish(GreenEnergyAgent myAgent) {
		super(myAgent);
		this.myGreenAgent = myAgent;
	}

	/**
	 * Method sends the information that the power shortage has finished and
	 * that the jobs which were kept on hold can now be supplied with green power
	 */
	@Override
	public void action() {
		logger.info(POWER_SHORTAGE_SOURCE_FINISH_LOG);
		final List<ServerJob> jobsOnHold = getJobsOnHold();

		if (jobsOnHold.isEmpty()) {
			logger.info(POWER_SHORTAGE_SOURCE_FINISH_NO_JOBS_LOG);
		} else {
			jobsOnHold.forEach(powerJob -> {
				MDC.put(MDC_JOB_ID, powerJob.getJobId());
				if (myGreenAgent.getServerJobs().containsKey(powerJob)) {
					logger.info(POWER_SHORTAGE_SOURCE_VERIFY_POWER_LOG, powerJob.getJobId());
					myAgent.addBehaviour(prepareVerificationBehaviour(powerJob));
				} else {
					logger.info(POWER_SHORTAGE_SOURCE_JOB_ENDED_LOG, powerJob.getJobId());
				}
			});
		}
		myGreenAgent.manageGreenPower()
				.setCurrentMaximumCapacity(myGreenAgent.manageGreenPower().getInitialMaximumCapacity());
	}

	private Behaviour prepareVerificationBehaviour(final ServerJob affectedJob) {
		final String conversationId = String.join("_", affectedJob.getJobId(),
				affectedJob.getStartTime().toString());
		return createWeatherRequest(myGreenAgent, ON_HOLD_JOB_CHECK_PROTOCOL, conversationId,
				getResponseHandler(affectedJob), getRequestRefuseHandler(affectedJob), affectedJob);
	}

	private BiConsumer<MonitoringData, IncorrectMessageContentException> getResponseHandler(final ServerJob job) {
		return (data, e) -> {
			MDC.put(MDC_JOB_ID, job.getJobId());

			if (nonNull(e)) {
				e.printStackTrace();
				logger.info(WEATHER_UNAVAILABLE_JOB_LOG, job.getJobId());
				return;
			}

			final Optional<Double> availablePower = myGreenAgent.manage().getAvailablePowerForJob(job, data, false);

			if (availablePower.isEmpty() || job.getPower() > availablePower.get()) {
				logger.info(NO_POWER_LEAVE_ON_HOLD_LOG, job.getJobId());
			} else {
				logger.info(CHANGE_JOB_STATUS_LOG, job.getJobId());
				final JobExecutionStatusEnum newStatus =
						isJobStarted(job, myGreenAgent.getServerJobs()) ? IN_PROGRESS : ACCEPTED;

				myGreenAgent.getServerJobs().replace(job, newStatus);
				myGreenAgent.manage().updateGreenSourceGUI();
				myGreenAgent.send(prepareJobPowerShortageInformation(mapToJobInstanceId(job), job.getServer(),
						POWER_SHORTAGE_FINISH_ALERT_PROTOCOL));
			}
		};
	}

	private Runnable getRequestRefuseHandler(final ServerJob affectedJob) {
		MDC.put(MDC_JOB_ID, affectedJob.getJobId());
		return () -> logger.info(WEATHER_UNAVAILABLE_JOB_LOG, affectedJob.getJobId());
	}

	private List<ServerJob> getJobsOnHold() {
		return myGreenAgent.getServerJobs().entrySet().stream()
				.filter(job -> (job.getValue().equals(ON_HOLD_PLANNED)
						|| job.getValue().equals(ON_HOLD)) &&
						job.getKey().getEndTime().isAfter(getCurrentTime()))
				.map(Map.Entry::getKey)
				.toList();
	}
}
