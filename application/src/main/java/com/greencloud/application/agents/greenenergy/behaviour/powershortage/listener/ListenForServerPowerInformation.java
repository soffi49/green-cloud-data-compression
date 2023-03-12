package com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener;

import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.RE_SUPPLY_FAILURE_JOB_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.RE_SUPPLY_FAILURE_NO_POWER_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.RE_SUPPLY_JOB_WITH_GREEN_ENERGY_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_JOB_RE_SUPPLY_REQUEST_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_JOB_RE_SUPPLY_REQUEST_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_POWER_SHORTAGE_FAILURE_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_POWER_SHORTAGE_FAILURE_PUT_ON_HOLD_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_POWER_SHORTAGE_FINISH_CHANGE_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_POWER_SHORTAGE_FINISH_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_POWER_SHORTAGE_START_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_POWER_SHORTAGE_START_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.WEATHER_UNAVAILABLE_RE_SUPPLY_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.templates.PowerShortageSourceMessageTemplates.SERVER_POWER_SHORTAGE_INFORMATION_TEMPLATE;
import static com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.RequestWeatherData.createWeatherRequest;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ALERT_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.NOT_ENOUGH_GREEN_POWER_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.PROCESSING_RE_SUPPLY_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.RE_SUPPLY_SUCCESSFUL_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.WEATHER_UNAVAILABLE_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDateAndServer;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_PLANNED;
import static jade.lang.acl.ACLMessage.AGREE;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;
import com.greencloud.commons.domain.job.ServerJob;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the power information coming from the owner Server
 */
public class ListenForServerPowerInformation extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForServerPowerInformation.class);

	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent agent executing the behaviour
	 */
	public ListenForServerPowerInformation(final Agent myAgent) {
		super(myAgent);
		this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
	}

	/**
	 * Method listens for the message coming from the Server passing the information regarding power
	 * supply changes:
	 *
	 * - information about power shortage start
	 * - informing about power shortage finish
	 * - informing about putting job on hold due to power shortage
	 */
	@Override
	public void action() {
		final ACLMessage msg = myAgent.receive(SERVER_POWER_SHORTAGE_INFORMATION_TEMPLATE);

		if (Objects.nonNull(msg)) {
			switch (msg.getProtocol()) {
				case SERVER_POWER_SHORTAGE_ALERT_PROTOCOL -> handleServerPowerShortageStart(msg);
				case POWER_SHORTAGE_FINISH_ALERT_PROTOCOL -> handleServerPowerShortageFinish(msg);
				case SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL -> handleServerJobTransferFailure(msg);
				case SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL -> handleJobReSupplyingWithGreenEnergy(msg);
			}
		} else {
			block();
		}
	}

	private void handleServerPowerShortageStart(final ACLMessage inform) {
		final JobPowerShortageTransfer powerShortageJob = readMessageContent(inform, JobPowerShortageTransfer.class);
		MDC.put(MDC_JOB_ID, powerShortageJob.getJobInstanceId().getJobId());
		final ServerJob affectedJob = getJobByIdAndStartDateAndServer(powerShortageJob.getJobInstanceId(),
				inform.getSender(), myGreenEnergyAgent.getServerJobs());

		if (Objects.nonNull(affectedJob)) {
			logger.info(SERVER_POWER_SHORTAGE_START_LOG, powerShortageJob.getJobInstanceId().getJobId());
			myGreenEnergyAgent.manage()
					.divideServerJobForPowerShortage(affectedJob, powerShortageJob.getPowerShortageStart());
		} else {
			logger.info(SERVER_POWER_SHORTAGE_START_NOT_FOUND_LOG, powerShortageJob.getJobInstanceId().getJobId());
		}
	}

	private void handleServerPowerShortageFinish(final ACLMessage inform) {
		final JobInstanceIdentifier jobInstanceId = readMessageContent(inform, JobInstanceIdentifier.class);
		final ServerJob serverJob = getJobByIdAndStartDateAndServer(jobInstanceId, inform.getSender(),
				myGreenEnergyAgent.getServerJobs());
		MDC.put(MDC_JOB_ID, jobInstanceId.getJobId());

		if (Objects.nonNull(serverJob)) {
			logger.info(SERVER_POWER_SHORTAGE_FINISH_CHANGE_LOG, jobInstanceId.getJobId());
			final JobExecutionStatusEnum newStatus =
					isJobStarted(serverJob, myGreenEnergyAgent.getServerJobs()) ? IN_PROGRESS : ACCEPTED;
			myGreenEnergyAgent.getServerJobs().replace(serverJob, newStatus);
			myGreenEnergyAgent.manage().updateGreenSourceGUI();
		} else {
			logger.info(SERVER_POWER_SHORTAGE_FINISH_NOT_FOUND_LOG, jobInstanceId.getJobId());
		}
	}

	private void handleServerJobTransferFailure(final ACLMessage inform) {
		final JobPowerShortageTransfer powerShortageJob = readMessageContent(inform, JobPowerShortageTransfer.class);
		final JobInstanceIdentifier jobInstanceId = powerShortageJob.getJobInstanceId();
		final ServerJob jobToPutOnHold = getJobByIdAndStartDateAndServer(jobInstanceId, inform.getSender(),
				myGreenEnergyAgent.getServerJobs());
		MDC.put(MDC_JOB_ID, jobInstanceId.getJobId());

		if (Objects.nonNull(jobToPutOnHold)) {
			final boolean hasStarted = isJobStarted(jobToPutOnHold, myGreenEnergyAgent.getServerJobs());
			logger.info(SERVER_POWER_SHORTAGE_FAILURE_PUT_ON_HOLD_LOG, jobInstanceId.getJobId());
			myGreenEnergyAgent.getServerJobs().replace(jobToPutOnHold,
					hasStarted ? JobExecutionStatusEnum.ON_HOLD : JobExecutionStatusEnum.ON_HOLD_PLANNED);
			myGreenEnergyAgent.manage().updateGreenSourceGUI();
		} else {
			logger.info(SERVER_POWER_SHORTAGE_FAILURE_NOT_FOUND_LOG, jobInstanceId.getJobId());
		}
	}

	private void handleJobReSupplyingWithGreenEnergy(final ACLMessage request) {
		final JobInstanceIdentifier jobInstanceId = readMessageContent(request, JobInstanceIdentifier.class);
		final ServerJob jobToCheck = getJobByIdAndStartDateAndServer(jobInstanceId, request.getSender(),
				myGreenEnergyAgent.getServerJobs());

		MDC.put(MDC_JOB_ID, jobInstanceId.getJobId());
		if (Objects.nonNull(jobToCheck)) {
			logger.info(SERVER_JOB_RE_SUPPLY_REQUEST_LOG, jobInstanceId.getJobId());

			final ACLMessage reply = request.createReply();

			myGreenEnergyAgent.send(prepareStringReply(reply, PROCESSING_RE_SUPPLY_MESSAGE, AGREE));
			myGreenEnergyAgent.addBehaviour(
					createWeatherRequest(myGreenEnergyAgent, SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL,
							getResponseHandler(jobToCheck, reply), getRequestRefuseHandler(jobToCheck, reply),
							jobToCheck));
		} else {
			logger.info(SERVER_JOB_RE_SUPPLY_REQUEST_NOT_FOUND_LOG, jobInstanceId.getJobId());
			myGreenEnergyAgent.send(prepareStringReply(request.createReply(), JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
		}
	}

	private BiConsumer<MonitoringData, IncorrectMessageContentException> getResponseHandler(final ServerJob job,
			final ACLMessage reply) {
		return (data, e) -> {
			final Optional<Double> availablePower = myGreenEnergyAgent.manage()
					.getAvailablePowerForJob(job, data, false);

			MDC.put(MDC_JOB_ID, job.getJobId());
			if (availablePower.isEmpty() || job.getPower() > availablePower.get()) {
				var power = availablePower.orElse(0D);
				logger.info(RE_SUPPLY_FAILURE_NO_POWER_JOB_LOG, job.getPower(), power, job.getJobId());
				myGreenEnergyAgent.send(prepareReply(reply, NOT_ENOUGH_GREEN_POWER_CAUSE_MESSAGE, FAILURE));
			} else {
				if (myGreenEnergyAgent.getServerJobs().containsKey(job)) {
					logger.info(RE_SUPPLY_JOB_WITH_GREEN_ENERGY_LOG, job.getJobId());

					myGreenEnergyAgent.getServerJobs().replace(job, ON_HOLD, IN_PROGRESS);
					myGreenEnergyAgent.getServerJobs().replace(job, ON_HOLD_PLANNED, ACCEPTED);
					myGreenEnergyAgent.manage().updateGreenSourceGUI();

					myGreenEnergyAgent.send(prepareReply(reply, RE_SUPPLY_SUCCESSFUL_MESSAGE, INFORM));
				} else {
					logger.info(RE_SUPPLY_FAILURE_JOB_NOT_FOUND_LOG, job.getJobId());
					myGreenEnergyAgent.send(prepareReply(reply, JOB_NOT_FOUND_CAUSE_MESSAGE, FAILURE));
				}
			}
		};
	}

	private Runnable getRequestRefuseHandler(final ServerJob job, final ACLMessage reply) {
		return () -> {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(WEATHER_UNAVAILABLE_RE_SUPPLY_JOB_LOG, job.getJobId());
			myGreenEnergyAgent.send(prepareReply(reply, WEATHER_UNAVAILABLE_CAUSE_MESSAGE, FAILURE));
		};
	}
}
