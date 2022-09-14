package com.greencloud.application.agents.greenenergy.behaviour.powersupply.initiator;

import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.util.Objects.isNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.handler.HandleManualPowerSupplyFinish;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.initiator.logs.PowerSupplyInitiatorLog;
import com.greencloud.application.agents.greenenergy.domain.GreenEnergyAgentConstants;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobStatusEnum;
import com.greencloud.application.domain.job.JobWithProtocol;
import com.greencloud.application.domain.job.PowerJob;
import com.greencloud.application.mapper.JobMapper;
import com.greencloud.application.messages.MessagingUtils;
import com.greencloud.application.messages.domain.factory.ReplyMessageFactory;
import com.greencloud.application.utils.TimeUtils;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

/**
 * Behaviour sends the proposal with power supply offer to Server Agent and handles the retrieved responses.
 */
public class InitiatePowerSupplyOffer extends ProposeInitiator {

	private static final Logger logger = LoggerFactory.getLogger(InitiatePowerSupplyOffer.class);
	private final String guid;
	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent agent which is executing the behaviour
	 * @param msg   proposal message that is sent to the Server Agent
	 */
	public InitiatePowerSupplyOffer(final Agent agent, final ACLMessage msg) {
		super(agent, msg);
		this.myGreenEnergyAgent = (GreenEnergyAgent) agent;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method handles ACCEPT_PROPOSAL response from server.
	 * It updates the state of the job in green source and replies with the message with correct protocol and the information
	 * that the execution of the given job can be started.
	 *
	 * @param accept_proposal accept proposal response retrieved from the Server Agent
	 */
	@Override
	protected void handleAcceptProposal(final ACLMessage accept_proposal) {
		final JobWithProtocol jobWithProtocol = MessagingUtils.readMessageContent(accept_proposal, JobWithProtocol.class);
		final PowerJob job = findCorrespondingJob(jobWithProtocol.getJobInstanceIdentifier());
		final Behaviour manualFinishBehaviour = new HandleManualPowerSupplyFinish(myGreenEnergyAgent,
				calculateExpectedJobEndTime(job), JobMapper.mapToJobInstanceId(job));

		myGreenEnergyAgent.getPowerJobs().replace(job, JobStatusEnum.ACCEPTED);
		myAgent.addBehaviour(manualFinishBehaviour);

		logger.info(PowerSupplyInitiatorLog.SEND_POWER_SUPPLY_RESPONSE_LOG, guid, job.getJobId());
		displayMessageArrow(myGreenEnergyAgent, accept_proposal.getSender());
		sendResponseToServer(accept_proposal, jobWithProtocol);
	}

	/**
	 * Method handles REJECT_PROPOSAL response from server.
	 *
	 * @param reject_proposal reject proposal response retrieved from the Server Agent
	 */
	@Override
	protected void handleRejectProposal(final ACLMessage reject_proposal) {
		logger.info(PowerSupplyInitiatorLog.POWER_SUPPLY_PROPOSAL_REJECTED_LOG, guid);
		final JobInstanceIdentifier jobInstanceId = MessagingUtils.readMessageContent(reject_proposal, JobInstanceIdentifier.class);
		final PowerJob powerJob = myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobInstanceId);
		if (Objects.nonNull(powerJob)) {
			myGreenEnergyAgent.getPowerJobs().remove(powerJob);
		}
	}

	private PowerJob findCorrespondingJob(final JobInstanceIdentifier jobInstance) {
		PowerJob job = myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobInstance);
		if (isNull(job)) {
			job = myGreenEnergyAgent.manage().getJobById(jobInstance.getJobId());
		}
		return job;
	}

	private void sendResponseToServer(final ACLMessage acceptProposal, final JobWithProtocol jobWithProtocol) {
		final ACLMessage response = ReplyMessageFactory.prepareReply(acceptProposal.createReply(),
				jobWithProtocol.getJobInstanceIdentifier(), INFORM);
		response.setProtocol(jobWithProtocol.getReplyProtocol());
		myAgent.send(response);
	}

	private Date calculateExpectedJobEndTime(final PowerJob job) {
		final Instant endDate = TimeUtils.getCurrentTime().isAfter(job.getEndTime()) ? TimeUtils.getCurrentTime() : job.getEndTime();
		return Date.from(endDate.plus(GreenEnergyAgentConstants.MAX_ERROR_IN_JOB_FINISH, ChronoUnit.MILLIS));
	}
}
