package com.greencloud.application.agents.server.management;

import static com.greencloud.application.agents.server.management.logs.ServerManagementLog.DISABLE_SERVER_LOG;
import static com.greencloud.application.utils.AlgorithmUtils.nextFibonacci;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.adaptation.InitiateServerDisabling;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ServerAdaptationManagement {

	private static final Logger logger = LoggerFactory.getLogger(ServerAdaptationManagement.class);

	private final ServerAgent serverAgent;

	public ServerAdaptationManagement(ServerAgent serverAgent) {
		this.serverAgent = serverAgent;
	}

	/**
	 * Method updates the weight of selection for given Green Energy Agent
	 *
	 * @param targetGreenSource Green Energy Agent of interest
	 * @return information if adaptation was successful
	 */
	public boolean changeGreenSourceWeights(String targetGreenSource) {
		var newWeights = new HashMap<>(serverAgent.manageConfig().getWeightsForGreenSourcesMap());

		if (newWeights.entrySet().stream().noneMatch(entry -> entry.getKey().getName().equals(targetGreenSource))) {
			return false;
		}

		newWeights.entrySet().forEach(entry -> increaseWeight(entry, targetGreenSource));
		serverAgent.manageConfig().setWeightsForGreenSourcesMap(newWeights);
		return true;
	}

	/**
	 * Method disables Server and passes the information to parent CNA
	 *
	 * @param adaptationMessage original adaptation request
	 */
	public void disableServer(final ACLMessage adaptationMessage) {
		logger.info(DISABLE_SERVER_LOG, serverAgent.getOwnerCloudNetworkAgent().getLocalName());
		serverAgent.disable();
		serverAgent.manage().writeStateToDatabase();
		serverAgent.addBehaviour(InitiateServerDisabling.create(serverAgent, adaptationMessage));
	}

	private void increaseWeight(Map.Entry<AID, Integer> entry, String targetGreenSource) {
		if (!entry.getKey().getName().equals(targetGreenSource)) {
			entry.setValue(nextFibonacci(entry.getValue()));
		}
	}
}
