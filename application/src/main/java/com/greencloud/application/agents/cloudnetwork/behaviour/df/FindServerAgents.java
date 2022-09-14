package com.greencloud.application.agents.cloudnetwork.behaviour.df;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.logs.CloudNetworkDFLog;
import com.greencloud.application.yellowpages.YellowPagesService;
import com.greencloud.application.yellowpages.domain.DFServiceConstants;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviours finds corresponding server com.greencloud.application.agents for given Cloud Network Agent in DF
 */
public class FindServerAgents extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(FindServerAgents.class);

	private CloudNetworkAgent myCloudNetworkAgent;
	private String guid;

	/**
	 * Method casts the agent to the agent of type CloudNetworkAgent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
		this.guid = myAgent.getName();
	}

	/**
	 * Method searches the Directory Facilitator for the corresponding to the given Cloud Network Agent servers
	 */
	@Override
	public void action() {
		final List<AID> serverAgents = YellowPagesService.search(myAgent, DFServiceConstants.SA_SERVICE_TYPE, myAgent.getName());

		if (serverAgents.isEmpty()) {
			logger.info(CloudNetworkDFLog.NO_SERVERS_FOUND_LOG, guid);
			myCloudNetworkAgent.doDelete();
		}
		myCloudNetworkAgent.setOwnedServers(serverAgents);
	}
}
