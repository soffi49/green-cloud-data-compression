package com.greencloud.application.agents.scheduler.behaviour.df;

import static com.greencloud.application.agents.scheduler.behaviour.df.logs.SchedulerDFLog.RECEIVE_CNA_ANNOUNCEMENT_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.df.logs.SchedulerDFLog.RECEIVE_CNA_DEREGISTRATION_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.df.logs.SchedulerDFLog.SUBSCRIBE_CNA_SERVICE_LOG;
import static com.greencloud.application.yellowpages.YellowPagesService.prepareSubscription;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import org.slf4j.Logger;

import com.greencloud.application.agents.scheduler.SchedulerAgent;
import com.greencloud.application.behaviours.df.AbstractSubscriptionInitiator;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Behaviours subscribes CNA service in the DF
 */
public class SubscribeCloudNetworkService extends AbstractSubscriptionInitiator {

	private static final Logger logger = getLogger(SubscribeCloudNetworkService.class);

	private final SchedulerAgent mySchedulerAgent;

	private SubscribeCloudNetworkService(final SchedulerAgent agent, ACLMessage subscription) {
		super(agent, subscription);
		this.mySchedulerAgent = (SchedulerAgent) myAgent;
	}

	/**
	 * Method creates behaviour
	 *
	 * @param agent scheduler subscribing to DF
	 * @return InitiateServiceSubscription
	 */
	public static SubscribeCloudNetworkService create(final SchedulerAgent agent) {
		logger.info(SUBSCRIBE_CNA_SERVICE_LOG);
		return new SubscribeCloudNetworkService(agent, prepareSubscription(agent, CNA_SERVICE_TYPE));
	}

	@Override
	protected void handleAddedAgents(Map<AID, Boolean> addedAgents) {
		logger.info(RECEIVE_CNA_ANNOUNCEMENT_LOG, addedAgents.size());
		mySchedulerAgent.getAvailableCloudNetworks().addAll(addedAgents.keySet());
	}

	@Override
	protected void handleRemovedAgents(Map<AID, Boolean> removedAgents) {
		logger.info(RECEIVE_CNA_DEREGISTRATION_LOG, removedAgents.size());
		mySchedulerAgent.getAvailableCloudNetworks().removeAll(removedAgents.keySet());
	}
}
