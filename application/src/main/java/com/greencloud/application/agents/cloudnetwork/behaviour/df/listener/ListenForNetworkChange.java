package com.greencloud.application.agents.cloudnetwork.behaviour.df.listener;

import static com.google.common.collect.Sets.symmetricDifference;
import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.logs.CloudNetworkDFListenerLog.FOUND_NEW_SERVERS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.logs.CloudNetworkDFListenerLog.FOUND_REMOVED_SERVERS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.templates.DFCloudNetworkMessageTemplates.ANNOUNCE_NETWORK_CHANGE_TEMPLATE;
import static com.greencloud.application.agents.cloudnetwork.domain.CloudNetworkPowerUpdateType.DECREMENT_CAPACITY;
import static com.greencloud.application.agents.cloudnetwork.domain.CloudNetworkPowerUpdateType.INCREMENT_CAPACITY;
import static com.greencloud.application.yellowpages.YellowPagesService.search;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SA_SERVICE_TYPE;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.AskServerForPowerInformation;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for changes in network structure (particularly connection of servers with CNA)
 */
public class ListenForNetworkChange extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForNetworkChange.class);

	private CloudNetworkAgent myCloudNetworkAgent;

	@Override
	public void onStart() {
		super.onStart();
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * When triggered adds new servers and removes deleted ones
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(ANNOUNCE_NETWORK_CHANGE_TEMPLATE);

		if (Objects.nonNull(message)) {
			final Set<AID> serverAgents = search(myAgent, SA_SERVICE_TYPE, myAgent.getName());
			final Set<AID> presentServers = new HashSet<>(myCloudNetworkAgent.getOwnedServers().keySet());
			final Set<AID> symmetricDifference = symmetricDifference(serverAgents, presentServers);

			final Map<AID, Boolean> addedServers = symmetricDifference.stream()
					.filter(serverAgents::contains)
					.collect(toMap(aid -> aid, aid -> true));
			final Set<AID> removedServers = symmetricDifference.stream()
					.filter(presentServers::contains)
					.collect(toSet());

			if (!addedServers.isEmpty()) {
				handleAddedServers(addedServers);
			}

			if (!removedServers.isEmpty()) {
				handleRemovedServers(removedServers);
			}
		}
	}

	private void handleAddedServers(final Map<AID, Boolean> addedServers) {
		logger.info(FOUND_NEW_SERVERS_LOG, addedServers.size());
		initializeWeights(addedServers.keySet());
		myCloudNetworkAgent.getOwnedServers().putAll(addedServers);
		myCloudNetworkAgent.addBehaviour(
				AskServerForPowerInformation.create(myCloudNetworkAgent, addedServers.keySet(),
						INCREMENT_CAPACITY));
	}

	private void handleRemovedServers(Set<AID> removedServers) {
		logger.info(FOUND_REMOVED_SERVERS_LOG, removedServers.size());
		myCloudNetworkAgent.getOwnedServers().entrySet().removeIf(server -> removedServers.contains(server.getKey()));
		removedServers.forEach(myCloudNetworkAgent.manageConfig().getWeightsForServersMap()::remove);
		myCloudNetworkAgent.addBehaviour(
				AskServerForPowerInformation.create(myCloudNetworkAgent, removedServers, DECREMENT_CAPACITY));
	}

	private void initializeWeights(Set<AID> addedServers) {
		addedServers.forEach(server -> myCloudNetworkAgent.manageConfig().getWeightsForServersMap().put(server, 1));
	}
}
