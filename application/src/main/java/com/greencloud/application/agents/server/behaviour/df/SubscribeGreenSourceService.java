package com.greencloud.application.agents.server.behaviour.df;

import static com.greencloud.application.agents.server.behaviour.df.logs.ServerDFLog.RECEIVE_GS_ANNOUNCEMENT_LOG;
import static com.greencloud.application.agents.server.behaviour.df.logs.ServerDFLog.RECEIVE_GS_DEREGISTRATION_LOG;
import static com.greencloud.application.agents.server.behaviour.df.logs.ServerDFLog.SUBSCRIBE_GS_SERVICE_LOG;
import static com.greencloud.application.yellowpages.YellowPagesService.prepareSubscription;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.GS_SERVICE_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import org.slf4j.Logger;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.behaviours.df.AbstractSubscriptionInitiator;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Behaviours subscribes Green Source service in the DF
 */
public class SubscribeGreenSourceService extends AbstractSubscriptionInitiator {

	private static final Logger logger = getLogger(SubscribeGreenSourceService.class);

	private final ServerAgent myServerAgent;

	private SubscribeGreenSourceService(final ServerAgent agent, ACLMessage subscription) {
		super(agent, subscription);
		this.myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method creates behaviour
	 *
	 * @param agent server subscribing to DF
	 * @return SubscribeGreenSourceService
	 */
	public static SubscribeGreenSourceService create(final ServerAgent agent) {
		logger.info(SUBSCRIBE_GS_SERVICE_LOG);
		return new SubscribeGreenSourceService(agent, prepareSubscription(agent, GS_SERVICE_TYPE, agent.getName()));
	}

	@Override
	protected void handleAddedAgents(Map<AID, Boolean> addedAgents) {
		logger.info(RECEIVE_GS_ANNOUNCEMENT_LOG, addedAgents.size());
		myServerAgent.adapt().connectNewGreenSourcesToServer(addedAgents.keySet().stream().toList());
	}

	@Override
	protected void handleRemovedAgents(Map<AID, Boolean> removedAgents) {
		logger.info(RECEIVE_GS_DEREGISTRATION_LOG, removedAgents.size());
		removedAgents.keySet().forEach(agent -> {
			myServerAgent.getOwnedGreenSources().remove(agent);
			myServerAgent.getWeightsForGreenSourcesMap().remove(agent);
		});
	}
}
