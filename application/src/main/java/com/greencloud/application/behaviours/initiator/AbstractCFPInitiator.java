package com.greencloud.application.behaviours.initiator;

import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.MessagingUtils.isMessageContentValid;
import static com.greencloud.application.utils.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareReply;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static java.util.Objects.isNull;

import java.util.Vector;
import java.util.function.BiFunction;

import org.slf4j.MDC;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.application.domain.job.JobInstanceIdentifier;

import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

/**
 * Abstract behaviour defines common CFP handling procedure.
 */
public abstract class AbstractCFPInitiator<T> extends ContractNetInitiator {

	protected final AbstractAgent myAbstractAgent;
	protected final ACLMessage originalMessage;
	protected final JobInstanceIdentifier jobInstance;
	protected final BiFunction<ACLMessage, ACLMessage, Integer> offerComparator;
	protected final Class<T> offerDataClass;
	protected ACLMessage bestProposal;

	/**
	 * Behaviour constructor
	 *
	 * @param agent           agent executing the behaviour
	 * @param cfp             CFP sent to owned agents
	 * @param originalMessage message which retrieval triggered CFP
	 * @param jobInstance     job of interest
	 * @param offerComparator comparator used to evaluate received proposals
	 */
	protected AbstractCFPInitiator(final AbstractAgent agent, final ACLMessage cfp, final ACLMessage originalMessage,
			final JobInstanceIdentifier jobInstance, final BiFunction<ACLMessage, ACLMessage, Integer> offerComparator,
			final Class<T> dataClass) {
		super(agent, cfp);

		this.myAbstractAgent = agent;
		this.originalMessage = originalMessage;
		this.jobInstance = jobInstance;
		this.offerComparator = offerComparator;
		this.offerDataClass = dataClass;
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
		if (offerComparator.apply(bestProposal, propose) < 0) {
			myAbstractAgent.send(prepareReply(bestProposal, jobInstance, REJECT_PROPOSAL));
			bestProposal = propose;
		} else {
			myAbstractAgent.send(prepareReply(propose, jobInstance, REJECT_PROPOSAL));
		}
	}

	/**
	 * Generic way of handling all received responses.
	 * Method handles 4 cases:
	 * <p> 1) case when no responses were retrieved </p>
	 * <p> 2) case when all agents refused to provide a given service </p>
	 * <p> 3) case when there is some best proposal but the content of the message is incorrect </p>
	 * <p> 4) case when there is some best proposal and it has correct structure </p>
	 * <p> In order to handle the aforementioned cases the method calls appropriate abstract handlers that are
	 * custom overwritten in classes that extend this behaviour. </p>
	 *
	 * @param responses   retrieved responses
	 * @param acceptances vector containing accept proposal message sent back to the chosen server (not used)
	 */
	@Override
	protected void handleAllResponses(final Vector responses, final Vector acceptances) {
		MDC.put(MDC_JOB_ID, jobInstance.getJobId());

		if (responses.isEmpty()) {
			this.handleNoResponses();
		} else if (isNull(bestProposal)) {
			this.handleNoAvailableAgents();
		} else {
			if (isMessageContentValid(bestProposal, offerDataClass)) {
				final T chosenOfferData = readMessageContent(bestProposal, offerDataClass);
				this.handleSelectedOffer(chosenOfferData);
			} else {
				myAgent.send(prepareReply(bestProposal, jobInstance, REJECT_PROPOSAL));
				this.handleNoAvailableAgents();
			}
		}
		myAbstractAgent.removeBehaviour(this);
	}

	/**
	 * Abstract method responsible for handling case when no responses were retrieved
	 */
	protected abstract void handleNoResponses();

	/**
	 * Abstract method responsible for handling case when all Servers refused to provide given service
	 */
	protected abstract void handleNoAvailableAgents();

	/**
	 * Abstract method responsible for handling what should happen with the selected Server offer
	 */
	protected abstract void handleSelectedOffer(final T chosenOfferData);

}
