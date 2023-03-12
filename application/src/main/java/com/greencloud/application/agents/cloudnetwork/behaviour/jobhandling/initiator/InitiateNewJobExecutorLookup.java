package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator;

import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.CHOSEN_SERVER_FOR_JOB_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.INCORRECT_PROPOSAL_FORMAT_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.NO_SERVER_AVAILABLE_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.NO_SERVER_RESPONSES_LOG;
import static com.greencloud.application.agents.cloudnetwork.domain.CloudNetworkAgentConstants.MAX_POWER_DIFFERENCE;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.mapper.JsonMapper.getMapper;
import static com.greencloud.application.messages.MessagingUtils.isMessageContentValid;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.MessagingUtils.rejectJobOffers;
import static com.greencloud.application.messages.domain.factory.OfferMessageFactory.makeJobOfferForClient;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.domain.ServerData;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

/**
 * Behaviour initiates lookup for Server which will execute the Client's job
 */
public class InitiateNewJobExecutorLookup extends ContractNetInitiator {

	private static final Logger logger = LoggerFactory.getLogger(InitiateNewJobExecutorLookup.class);

	private final ACLMessage replyMessage;
	private final CloudNetworkAgent myCloudNetworkAgent;
	private final ClientJob job;
	private ACLMessage bestProposal;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent           agent which is executing the behaviour
	 * @param cfp             call for proposal message containing job requriements sent to the servers
	 * @param originalMessage original message received from the client
	 * @param job             job of interest
	 */
	public InitiateNewJobExecutorLookup(final Agent agent, final ACLMessage cfp, final ACLMessage originalMessage,
			ClientJob job) {
		super(agent, cfp);
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
		this.replyMessage = originalMessage.createReply();
		this.job = job;
	}

	/**
	 * Method waits for all Server Agent responses.
	 * Upon receiving all response it responds with PROPOSE to the scheduler if there is
	 * a server with the best proposal or with REFUSE otherwise.
	 *
	 * @param responses   retrieved responses from Server Agents
	 * @param acceptances vector containing accept proposal message sent back to the chosen server (not used)
	 */
	@Override
	protected void handleAllResponses(final Vector responses, final Vector acceptances) {
		if (!myCloudNetworkAgent.getNetworkJobs().containsKey(job)) {
			return;
		}

		MDC.put(MDC_JOB_ID, job.getJobId());
		if (responses.isEmpty()) {
			logger.info(NO_SERVER_RESPONSES_LOG);
			handleRejectedJob();
		} else if (isNull(bestProposal)) {
			logger.info(NO_SERVER_AVAILABLE_LOG);
			handleRejectedJob();
		} else {
			if (isMessageContentValid(bestProposal, ServerData.class)) {
				final ServerData chosenServerData = readMessageContent(bestProposal, ServerData.class);
				final double availablePower =
						myCloudNetworkAgent.getMaximumCapacity() - myCloudNetworkAgent.manage().getCurrentPowerInUse();

				logger.info(CHOSEN_SERVER_FOR_JOB_LOG, job.getJobId(), bestProposal.getSender().getName());

				final ACLMessage reply = bestProposal.createReply();
				final ACLMessage offer = makeJobOfferForClient(chosenServerData, availablePower, replyMessage);

				myCloudNetworkAgent.getServerForJobMap().put(job.getJobId(), bestProposal.getSender());
				myCloudNetworkAgent.addBehaviour(new InitiateMakingNewJobOffer(myCloudNetworkAgent, offer, reply));
			} else {
				handleInvalidResponse(bestProposal);
			}
		}
	}

	/**
	 * Method verifies if newly received proposal is better than the current best one
	 *
	 * @param propose     received proposal
	 * @param acceptances vector containing accept proposal message sent back to the chosen server (not used)
	 */
	@Override
	protected void handlePropose(final ACLMessage propose, final Vector acceptances) {
		if (isNull(bestProposal)) {
			bestProposal = propose;
			return;
		}
		if (compareServerOffers(bestProposal, propose) < 0) {
			myCloudNetworkAgent.send(
					prepareReply(bestProposal.createReply(), mapToJobInstanceId(job), REJECT_PROPOSAL));
			bestProposal = propose;
		} else {
			myCloudNetworkAgent.send(prepareReply(propose.createReply(), mapToJobInstanceId(job), REJECT_PROPOSAL));
		}
	}

	private void handleRejectedJob() {
		myCloudNetworkAgent.getNetworkJobs().remove(job);
		myCloudNetworkAgent.manage().updateCloudNetworkGUI();
		myAgent.send(prepareRefuseReply(replyMessage));
	}

	private void handleInvalidResponse(final ACLMessage proposal) {
		logger.info(INCORRECT_PROPOSAL_FORMAT_LOG);
		rejectJobOffers(myCloudNetworkAgent, mapToJobInstanceId(job), null, singletonList(proposal));
		myAgent.send(prepareRefuseReply(replyMessage));
	}

	private int compareServerOffers(final ACLMessage serverOffer1, final ACLMessage serverOffer2) {
		ServerData server1;
		ServerData server2;
		int weight1 = myCloudNetworkAgent.manageConfig().getWeightsForServersMap().get(serverOffer1.getSender());
		int weight2 = myCloudNetworkAgent.manageConfig().getWeightsForServersMap().get(serverOffer2.getSender());
		try {
			server1 = getMapper().readValue(serverOffer1.getContent(), ServerData.class);
			server2 = getMapper().readValue(serverOffer2.getContent(), ServerData.class);
		} catch (JsonProcessingException e) {
			return Integer.MAX_VALUE;
		}
		int powerDifference = (server2.getAvailablePower() * weight2) - (server1.getAvailablePower() * weight1);
		int priceDifference = (int) ((server1.getServicePrice() * 1 / weight1) - (server2.getServicePrice() * 1
				/ weight2));
		return MAX_POWER_DIFFERENCE.isValidIntValue(powerDifference) ? priceDifference : powerDifference;
	}
}
