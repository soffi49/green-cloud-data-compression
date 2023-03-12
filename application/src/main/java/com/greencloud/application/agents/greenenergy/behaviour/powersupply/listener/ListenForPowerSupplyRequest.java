package com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener;

import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.INCORRECT_WEATHER_DATA_FORMAT_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.NOT_ENOUGH_POWER_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.POWER_SUPPLY_PROPOSAL_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.TOO_BAD_WEATHER_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.WEATHER_UNAVAILABLE_FOR_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.template.PowerSupplyMessageTemplates.POWER_SUPPLY_REQUEST_TEMPLATE;
import static com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.RequestWeatherData.createWeatherRequest;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToServerJob;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareRefuseReply;
import static java.util.Objects.nonNull;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.initiator.InitiatePowerSupplyOffer;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.application.messages.domain.factory.OfferMessageFactory;
import com.greencloud.application.messages.domain.factory.ReplyMessageFactory;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.ServerJob;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the power supply request coming from parent Server Agent
 */
public class ListenForPowerSupplyRequest extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForPowerSupplyRequest.class);

	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviours constructor.
	 *
	 * @param myAgent agent which is executing the behaviour
	 */
	public ListenForPowerSupplyRequest(Agent myAgent) {
		this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
	}

	/**
	 * Method listens for the power CFP coming from the server.
	 * It analyzes the request and either rejects it or proceeds with request processing by sending
	 * another request to Monitoring Agent for the com.greencloud.application.weather data.
	 */
	@Override
	public void action() {
		final ACLMessage cfp = myAgent.receive(POWER_SUPPLY_REQUEST_TEMPLATE);
		if (nonNull(cfp)) {
			final PowerJob job = readJob(cfp);
			if (nonNull(job)) {
				final ServerJob serverJob = mapToServerJob(job, cfp.getSender());
				myGreenEnergyAgent.getServerJobs().put(serverJob, ExecutionJobStatusEnum.PROCESSING);
				myAgent.addBehaviour(
						createWeatherRequest(myGreenEnergyAgent, cfp.getProtocol(), cfp.getConversationId(),
								getResponseHandler(serverJob, cfp), getRefuseResponseHandler(serverJob, cfp),
								serverJob));
			}
		} else {
			block();
		}
	}

	private PowerJob readJob(final ACLMessage callForProposal) {
		try {
			return readMessageContent(callForProposal, PowerJob.class);
		} catch (Exception e) {
			logger.info("I didn't understand the message from the server, refusing the job");
			myAgent.send(prepareRefuseReply(callForProposal.createReply()));
		}
		return null;
	}

	private BiConsumer<MonitoringData, IncorrectMessageContentException> getResponseHandler(final ServerJob job,
			final ACLMessage cfp) {
		return (data, e) -> {
			MDC.put(MDC_JOB_ID, job.getJobId());

			if (nonNull(e)) {
				e.printStackTrace();
				logger.info(INCORRECT_WEATHER_DATA_FORMAT_LOG);
				handleRefusal(job, cfp);
				return;
			}

			final Optional<Double> averageAvailablePower = myGreenEnergyAgent.manage()
					.getAvailablePowerForJob(job, data, true);
			final String jobId = job.getJobId();

			if (averageAvailablePower.isEmpty()) {
				logger.info(TOO_BAD_WEATHER_LOG, jobId);
				handleRefusal(job, cfp);
			} else if (job.getPower() > averageAvailablePower.get()) {
				logger.info(NOT_ENOUGH_POWER_LOG, jobId, job.getPower(), averageAvailablePower.get());
				handleRefusal(job, cfp);
			} else {
				logger.info(POWER_SUPPLY_PROPOSAL_LOG, jobId);
				final ACLMessage offer = OfferMessageFactory.makeGreenEnergyPowerSupplyOffer(myGreenEnergyAgent,
						averageAvailablePower.get(), myGreenEnergyAgent.manage().computeCombinedPowerError(job),
						jobId, cfp.createReply());
				myAgent.addBehaviour(new InitiatePowerSupplyOffer(myAgent, offer, data));
			}
		};
	}

	private Runnable getRefuseResponseHandler(final ServerJob job,
			final ACLMessage cfp) {
		return () -> {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(WEATHER_UNAVAILABLE_FOR_JOB_LOG);
			handleRefusal(job, cfp);
		};
	}

	private void handleRefusal(final ServerJob job, final ACLMessage cfp) {
		myGreenEnergyAgent.manage().removeJob(job);
		myGreenEnergyAgent.send(ReplyMessageFactory.prepareRefuseReply(cfp.createReply()));
	}
}
