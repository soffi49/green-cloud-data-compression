package agents.cloudnetwork.behaviour.df;

import static agents.cloudnetwork.behaviour.df.logs.CloudNetworkDFLog.NO_SERVERS_FOUND_LOG;
import static yellowpages.YellowPagesService.search;
import static yellowpages.domain.DFServiceConstants.SA_SERVICE_TYPE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.cloudnetwork.CloudNetworkAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviours finds corresponding server agents for given Cloud Network Agent in DF
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
		final List<AID> serverAgents = search(myAgent, SA_SERVICE_TYPE, myAgent.getName());

		if (serverAgents.isEmpty()) {
			logger.info(NO_SERVERS_FOUND_LOG, guid);
			myCloudNetworkAgent.doDelete();
		}
		myCloudNetworkAgent.setOwnedServers(serverAgents);
	}
}
