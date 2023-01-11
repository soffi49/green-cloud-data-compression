package com.greencloud.application.agents.server.management;

import static com.greencloud.application.utils.AlgorithmUtils.nextFibonacci;

import java.util.HashMap;
import java.util.Map;

import com.greencloud.application.agents.server.ServerAgent;

import jade.core.AID;

public class ServerAdaptationManagement {

	private final ServerAgent serverAgent;

	public ServerAdaptationManagement(ServerAgent serverAgent) {
		this.serverAgent = serverAgent;
	}

	public boolean changeGreenSourceWeights(String targetGreenSource) {
		var newWeights = new HashMap<>(serverAgent.manageConfig().getWeightsForGreenSourcesMap());

		if (newWeights.entrySet().stream().noneMatch(entry -> entry.getKey().getName().equals(targetGreenSource))) {
			return false;
		}

		newWeights.entrySet().forEach(entry -> increaseWeight(entry, targetGreenSource));
		serverAgent.manageConfig().setWeightsForGreenSourcesMap(newWeights);
		return true;
	}

	private void increaseWeight(Map.Entry<AID, Integer> entry, String targetGreenSource) {
		if (!entry.getKey().getName().equals(targetGreenSource)) {
			entry.setValue(nextFibonacci(entry.getValue()));
		}
	}
}
