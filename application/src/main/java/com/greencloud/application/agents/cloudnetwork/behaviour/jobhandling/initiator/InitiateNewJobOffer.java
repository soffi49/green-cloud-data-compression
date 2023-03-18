package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator;

import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.ACCEPT_SERVER_PROPOSAL_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.REJECT_SERVER_PROPOSAL_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareAcceptJobOfferReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.utils.JobUtils.getJobById;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.ACCEPTED;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.domain.agent.ServerData;
import com.greencloud.application.domain.job.ImmutableJobWithPrice;
import com.greencloud.application.domain.job.JobWithPrice;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.message.MessageBuilder;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

/**
 * Behaviour sends proposal with job execution offer to the Scheduler
 */
public class InitiateNewJobOffer extends ProposeInitiator {

	private static final Logger logger = getLogger(InitiateNewJobOffer.class);

	private final ACLMessage serverMessage;
	private final CloudNetworkAgent myCloudNetworkAgent;

	protected InitiateNewJobOffer(final Agent agent, final ACLMessage msg, final ACLMessage serverMessage) {
		super(agent, msg);
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
		this.serverMessage = serverMessage;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param agent            agent creating a behaviour
	 * @param availablePower   current available power for given network segment
	 * @param serverOffer      job execution offer proposed by the server
	 * @param serverMessage    proposal message received from selected server
	 * @param schedulerMessage CFP received from scheduler
	 * @return InitiateMakingNewJobOffer
	 */
	public static InitiateNewJobOffer create(final Agent agent, final double availablePower,
			final ServerData serverOffer, final ACLMessage serverMessage, final ACLMessage schedulerMessage) {
		final JobWithPrice pricedJob =
				new ImmutableJobWithPrice(serverOffer.getJobId(), serverOffer.getServicePrice(), availablePower);
		final ACLMessage proposal = MessageBuilder.builder()
				.copy(schedulerMessage.createReply())
				.withObjectContent(pricedJob)
				.withPerformative(PROPOSE)
				.build();

		return new InitiateNewJobOffer(agent, proposal, serverMessage);
	}

	/**
	 * Method handles ACCEPT_PROPOSAL message retrieved from the Scheduler Agent.
	 * It sends accept proposal to the chosen for job execution Server Agent and updates the network state.
	 *
	 * @param accept received accept proposal message
	 */
	@Override
	protected void handleAcceptProposal(final ACLMessage accept) {
		final String jobId = accept.getContent();
		final ClientJob job = getJobById(jobId, myCloudNetworkAgent.getNetworkJobs());

		if (nonNull(job)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.info(ACCEPT_SERVER_PROPOSAL_LOG);

			myCloudNetworkAgent.manage().incrementJobCounter(jobId, ACCEPTED);
			myAgent.send(
					prepareAcceptJobOfferReply(serverMessage, mapToJobInstanceId(job), SERVER_JOB_CFP_PROTOCOL));
		}
	}

	/**
	 * Method handles REJECT_PROPOSAL message retrieved from the Scheduler Agent.
	 * It sends reject proposal to the Server Agent previously chosen for the job execution.
	 *
	 * @param reject received reject proposal message
	 */
	@Override
	protected void handleRejectProposal(final ACLMessage reject) {
		final String jobId = reject.getContent();
		final ClientJob job = getJobById(jobId, myCloudNetworkAgent.getNetworkJobs());
		MDC.put(MDC_JOB_ID, jobId);
		logger.info(REJECT_SERVER_PROPOSAL_LOG, reject.getSender().getName());

		if (nonNull(job)) {
			myCloudNetworkAgent.getServerForJobMap().remove(jobId);
			myCloudNetworkAgent.getNetworkJobs().remove(getJobById(jobId, myCloudNetworkAgent.getNetworkJobs()));
			myCloudNetworkAgent.send(prepareReply(serverMessage, mapToJobInstanceId(job), REJECT_PROPOSAL));
		}
	}
}
