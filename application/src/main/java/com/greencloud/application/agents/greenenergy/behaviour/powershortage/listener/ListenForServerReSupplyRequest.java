package com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener;

import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.RE_SUPPLY_FAILURE_JOB_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.RE_SUPPLY_FAILURE_NO_POWER_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.RE_SUPPLY_JOB_WITH_GREEN_ENERGY_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_JOB_RE_SUPPLY_REQUEST_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.SERVER_JOB_RE_SUPPLY_REQUEST_NOT_FOUND_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs.PowerShortageSourceListenerLog.WEATHER_UNAVAILABLE_RE_SUPPLY_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.templates.PowerShortageSourceMessageTemplates.SERVER_RE_SUPPLY_REQUEST_TEMPLATE;
import static com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.RequestWeatherData.createWeatherRequest;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.NOT_ENOUGH_GREEN_POWER_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.RE_SUPPLY_SUCCESSFUL_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.WEATHER_UNAVAILABLE_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDateAndServer;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_GREEN;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.weather.MonitoringData;
import com.greencloud.commons.domain.job.ServerJob;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles the requests coming from Server Agent asking to re-supply job with green energy
 */
public class ListenForServerReSupplyRequest extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForServerReSupplyRequest.class);

	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent agent executing the behaviour
	 */
	public ListenForServerReSupplyRequest(final Agent myAgent) {
		super(myAgent);
		this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
	}

	/**
	 * Method listens for the request to re-supply job with green energy. It sends the request to Monitoring Agent
	 * in order to check the weather conditions and based on them evaluates if it can agree to the Server's request.
	 */
	@Override
	public void action() {
		final ACLMessage request = myAgent.receive(SERVER_RE_SUPPLY_REQUEST_TEMPLATE);

		if (nonNull(request)) {
			final JobInstanceIdentifier jobInstanceId = readMessageContent(request, JobInstanceIdentifier.class);
			final ServerJob jobToCheck = getJobByIdAndStartDateAndServer(jobInstanceId, request.getSender(),
					myGreenEnergyAgent.getServerJobs());

			MDC.put(MDC_JOB_ID, jobInstanceId.getJobId());
			if (nonNull(jobToCheck)) {
				logger.info(SERVER_JOB_RE_SUPPLY_REQUEST_LOG, jobInstanceId.getJobId());
				myGreenEnergyAgent.addBehaviour(
						createWeatherRequest(myGreenEnergyAgent, SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL,
								getResponseHandler(jobToCheck, request), getRequestRefuseHandler(jobToCheck, request),
								jobToCheck));
			} else {
				logger.info(SERVER_JOB_RE_SUPPLY_REQUEST_NOT_FOUND_LOG, jobInstanceId.getJobId());
				myGreenEnergyAgent.send(prepareStringReply(request, JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
			}
		} else {
			block();
		}
	}

	private BiConsumer<MonitoringData, Exception> getResponseHandler(final ServerJob job, final ACLMessage request) {
		return (data, e) -> {
			final double availablePower = myGreenEnergyAgent.power().getAvailablePower(job, data, false).orElse(0D);

			MDC.put(MDC_JOB_ID, job.getJobId());
			if (job.getPower() > availablePower) {
				logger.info(RE_SUPPLY_FAILURE_NO_POWER_JOB_LOG, job.getPower(), availablePower, job.getJobId());
				myGreenEnergyAgent.send(prepareStringReply(request, NOT_ENOUGH_GREEN_POWER_CAUSE_MESSAGE, REFUSE));
			} else {
				if (myGreenEnergyAgent.getServerJobs().containsKey(job)) {
					logger.info(RE_SUPPLY_JOB_WITH_GREEN_ENERGY_LOG, job.getJobId());
					final boolean isActive = myGreenEnergyAgent.getServerJobs().get(job).equals(ON_HOLD);

					myGreenEnergyAgent.getServerJobs().replace(job, EXECUTING_ON_GREEN.getStatus(isActive));
					myGreenEnergyAgent.manage().updateGUI();

					myGreenEnergyAgent.send(prepareStringReply(request, RE_SUPPLY_SUCCESSFUL_MESSAGE, INFORM));
				} else {
					logger.info(RE_SUPPLY_FAILURE_JOB_NOT_FOUND_LOG, job.getJobId());
					myGreenEnergyAgent.send(prepareStringReply(request, JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
				}
			}
		};
	}

	private Runnable getRequestRefuseHandler(final ServerJob job, final ACLMessage request) {
		return () -> {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(WEATHER_UNAVAILABLE_RE_SUPPLY_JOB_LOG, job.getJobId());
			myGreenEnergyAgent.send(prepareStringReply(request, WEATHER_UNAVAILABLE_CAUSE_MESSAGE, FAILURE));
		};
	}
}
