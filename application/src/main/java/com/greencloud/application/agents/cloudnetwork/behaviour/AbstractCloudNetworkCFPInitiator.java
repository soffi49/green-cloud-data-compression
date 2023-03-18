package com.greencloud.application.agents.cloudnetwork.behaviour;

import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.isMessageContentValid;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.MessagingUtils.rejectJobOffers;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

import java.util.Vector;

import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.domain.agent.ServerData;
import com.greencloud.application.domain.job.JobInstanceIdentifier;

import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

/**
 * Abstract behaviour defines common CFP handling procedure for Cloud Network Agent.
 * Important! This abstract behaviour can be extended in all CFP that handles selection of Server proposals
 * (i.e. the data retrieved in messages must be of the type ServerData)
 */
public abstract class AbstractCloudNetworkCFPInitiator extends ContractNetInitiator {

	protected final CloudNetworkAgent myCloudNetworkAgent;
	protected final ACLMessage originalMessage;
	private final JobInstanceIdentifier jobInstanceId;
	protected ACLMessage bestProposal;

	/**
	 * Behaviour constructor
	 *
	 * @param agent           agent executing the behaviour
	 * @param cfp             CFP sent to Servers
	 * @param originalMessage message which retrieval triggered CFP in Cloud Network Agent
	 * @param jobInstanceId   unique identifier of the job of interest
	 */
	protected AbstractCloudNetworkCFPInitiator(final CloudNetworkAgent agent, final ACLMessage cfp,
			final ACLMessage originalMessage, final JobInstanceIdentifier jobInstanceId) {
		super(agent, cfp);

		this.myCloudNetworkAgent = agent;
		this.originalMessage = originalMessage;
		this.jobInstanceId = jobInstanceId;
	}

	/**
	 * Generic way of handling PROPOSE response.
	 * Method verifies if newly received proposal is better than the current best one and, upon that, updates
	 * the information regarding the best proposal and refuses the unselected one.
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
		if (myCloudNetworkAgent.manage().compareServerOffers(bestProposal, propose) < 0) {
			myCloudNetworkAgent.send(prepareReply(bestProposal, jobInstanceId, REJECT_PROPOSAL));
			bestProposal = propose;
		} else {
			myCloudNetworkAgent.send(prepareReply(propose, jobInstanceId, REJECT_PROPOSAL));
		}
	}

	/**
	 * Generic way of handling all received responses.
	 * Method handles 4 cases:
	 * <p> 1) case when no responses were retrieved from Servers </p>
	 * <p> 2) case when all Servers refused to provide a given service </p>
	 * <p> 3) case when there is some best server's proposal but the content of the message is incorrect </p>
	 * <p> 4) case when there is some best server's proposal and it has correct structure </p>
	 * <p> In order to handle the aforementioned cases the method calls appropriate abstract handlers that are
	 * custom overwritten in classes that extend this behaviour. </p>
	 *
	 * @param responses   retrieved responses from Server Agents
	 * @param acceptances vector containing accept proposal message sent back to the chosen server (not used)
	 */
	@Override
	protected void handleAllResponses(final Vector responses, final Vector acceptances) {
		MDC.put(MDC_JOB_ID, jobInstanceId.getJobId());

		if (responses.isEmpty()) {
			this.handleNoServerResponses();
		} else if (isNull(bestProposal)) {
			this.handleNoAvailableServers();
		} else {
			if (isMessageContentValid(bestProposal, ServerData.class)) {
				final ServerData chosenServerData = readMessageContent(bestProposal, ServerData.class);
				this.handleSelectedOffer(chosenServerData);
			} else {
				rejectJobOffers(myCloudNetworkAgent, jobInstanceId, null, singletonList(bestProposal));
				this.handleNoAvailableServers();
			}
		}
	}

	/**
	 * Abstract method responsible for handling case when no responses were retrieved
	 */
	protected abstract void handleNoServerResponses();

	/**
	 * Abstract method responsible for handling case when all Servers refused to provide given service
	 */
	protected abstract void handleNoAvailableServers();

	/**
	 * Abstract method responsible for handling what should happen with the selected Server offer
	 */
	protected abstract void handleSelectedOffer(final ServerData serverData);

}
