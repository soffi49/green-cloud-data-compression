package com.greencloud.application.agents.cloudnetwork.behaviour.df.subscribe;

import static com.greencloud.application.agents.cloudnetwork.behaviour.df.subscribe.logs.CloudNetworkDFSubscribeLog.NO_SCHEDULER_FOUND_LOG;
import static com.greencloud.application.yellowpages.YellowPagesService.search;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SCHEDULER_SERVICE_TYPE;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Set;

import org.slf4j.Logger;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviours finds corresponding Scheduler Agent for given Cloud Network Agent in DF
 */
public class FindSchedulerAgent extends OneShotBehaviour {

	private static final Logger logger = getLogger(FindSchedulerAgent.class);

	private CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Method casts the agent to the agent of type CloudNetwork Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * Method searches the Directory Facilitator for the corresponding Scheduler Agent service
	 */
	@Override
	public void action() {
		final Set<AID> schedulerAgents = search(myAgent, myCloudNetworkAgent.getParentDFAddress(),
				SCHEDULER_SERVICE_TYPE);

		if (schedulerAgents.isEmpty()) {
			logger.info(NO_SCHEDULER_FOUND_LOG);
			myCloudNetworkAgent.doDelete();
		}
		myCloudNetworkAgent.setScheduler(schedulerAgents.stream().findFirst().orElseThrow());
	}
}
