package com.greencloud.application.agents.server.management;

import static com.greencloud.application.agents.server.management.logs.ServerManagementLog.DISABLE_SERVER_LOG;
import static com.greencloud.application.agents.server.management.logs.ServerManagementLog.ENABLE_SERVER_LOG;
import static com.greencloud.application.utils.AlgorithmUtils.nextFibonacci;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;

import com.greencloud.application.agents.AbstractAgentManagement;
import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.adaptation.initiator.InitiateServerDisabling;
import com.greencloud.application.agents.server.behaviour.adaptation.initiator.InitiateServerEnabling;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Set of methods used to adapt the current configuration of Green Energy Agent
 */
public class ServerAdaptationManagement extends AbstractAgentManagement {

	private static final Logger logger = getLogger(ServerAdaptationManagement.class);

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
	public boolean changeGreenSourceWeights(final String targetGreenSource) {
		final ConcurrentHashMap<AID, Integer> newWeights =
				new ConcurrentHashMap<>(serverAgent.getWeightsForGreenSourcesMap());

		if (newWeights.keySet().stream().noneMatch(agent -> agent.getName().equals(targetGreenSource))) {
			return false;
		}

		newWeights.entrySet().forEach(entry -> increaseWeight(entry, targetGreenSource));
		serverAgent.setWeightsForGreenSourcesMap(newWeights);
		return true;
	}

	/**
	 * Method connects new green sources to the server agent
	 *
	 * @param newGreenSources list of green sources to connect to the server
	 */
	public void connectNewGreenSourcesToServer(final List<AID> newGreenSources) {
		final Map<AID, Boolean> greenSourceWithState = newGreenSources.stream().collect(toMap(gs -> gs, gs -> true));
		serverAgent.getOwnedGreenSources().putAll(greenSourceWithState);
		assignWeightsToNewGreenSources(newGreenSources);
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

	/**
	 * Method enables given Server and passes the information to parent CNA
	 *
	 * @param adaptationMessage original adaptation request
	 */
	public void enableServer(final ACLMessage adaptationMessage) {
		logger.info(ENABLE_SERVER_LOG, serverAgent.getOwnerCloudNetworkAgent().getLocalName());
		serverAgent.enable();
		serverAgent.manage().writeStateToDatabase();
		serverAgent.addBehaviour(InitiateServerEnabling.create(serverAgent, adaptationMessage));
	}

	private void increaseWeight(final Map.Entry<AID, Integer> entry, final String targetGreenSource) {
		if (!entry.getKey().getName().equals(targetGreenSource)) {
			entry.setValue(nextFibonacci(entry.getValue()));
		}
	}

	private void assignWeightsToNewGreenSources(final List<AID> newGreenSources) {
		final ConcurrentMap<AID, Integer> weightsMap = serverAgent.getWeightsForGreenSourcesMap();
		final int maxWeight = weightsMap.values().stream().max(Integer::compare).orElse(1);
		final int weight = serverAgent.getWeightsForGreenSourcesMap().isEmpty() ? 1 : maxWeight;

		newGreenSources.forEach(greenSource -> weightsMap.putIfAbsent(greenSource, weight));
	}
}
