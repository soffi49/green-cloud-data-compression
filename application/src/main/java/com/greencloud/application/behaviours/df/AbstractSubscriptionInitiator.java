package com.greencloud.application.behaviours.df;

import static com.greencloud.application.yellowpages.YellowPagesService.decodeSubscription;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import com.greencloud.application.agents.AbstractAgent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

/**
 * Abstract behaviour defining default subscription handling mechanism and methods
 */
public abstract class AbstractSubscriptionInitiator extends SubscriptionInitiator {

	protected AbstractSubscriptionInitiator(final AbstractAgent agent, ACLMessage subscription) {
		super(agent, subscription);
	}

	/**
	 * Method is triggered when agents register/deregister their services in DF.
	 * It groups the agents into 2 lists based on registration state (i.e. agents that registered service and agents
	 * that deregistered service). Then, it applies predefined handling methods.
	 *
	 * @param inform retrieved notification
	 */
	@Override
	protected void handleInform(final ACLMessage inform) {
		final Map<AID, Boolean> announcedAgents = decodeSubscription(inform);

		final Map<AID, Boolean> addedAgents = announcedAgents.entrySet().stream()
				.filter(Map.Entry::getValue)
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		final Map<AID, Boolean> removedAgents = announcedAgents.entrySet().stream()
				.filter(entry -> !entry.getValue())
				.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (!addedAgents.isEmpty()) {
			handleAddedAgents(addedAgents);
		}

		if (!removedAgents.isEmpty()) {
			handleRemovedAgents(removedAgents);
		}

	}

	/**
	 * Abstract method that can be overridden in order to process added agents
	 *
	 * @param addedAgents agents that introduced their services in DF
	 */
	protected void handleAddedAgents(final Map<AID, Boolean> addedAgents) {

	}

	/**
	 * Abstract method that can be overridden in order to process removed agents
	 *
	 * @param removedAgents agents that removed their services from DF
	 */
	protected void handleRemovedAgents(final Map<AID, Boolean> removedAgents) {

	}
}
