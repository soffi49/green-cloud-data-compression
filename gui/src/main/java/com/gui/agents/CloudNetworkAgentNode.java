package com.gui.agents;

import java.util.List;

import com.greencloud.commons.args.agent.cloudnetwork.ImmutableCloudNetworkNodeArgs;
import com.gui.message.ImmutableRegisterAgentMessage;
import com.gui.message.ImmutableSetNumericValueMessage;
import com.gui.websocket.GuiWebSocketClient;

/**
 * Agent node class representing the cloud network
 */
public class CloudNetworkAgentNode extends AbstractNetworkAgentNode {

	private final List<String> serverAgents;

	/**
	 * Cloud network node constructor
	 *
	 * @param name            name of the node
	 * @param maximumCapacity maximum capacity of cloud network
	 * @param serverAgents    list of server agents names
	 */
	public CloudNetworkAgentNode(String name, double maximumCapacity, List<String> serverAgents) {
		super(name, maximumCapacity);
		this.serverAgents = serverAgents;
	}

	@Override
	public void addToGraph(GuiWebSocketClient webSocketClient) {
		this.webSocketClient = webSocketClient;
		webSocketClient.send(ImmutableRegisterAgentMessage.builder()
				.agentType("CLOUD_NETWORK")
				.data(ImmutableCloudNetworkNodeArgs.builder()
						.name(agentName)
						.serverAgents(serverAgents)
						.maximumCapacity(initialMaximumCapacity.get())
						.build())
				.build());
	}

	/**
	 * Function updates the number of clients to given value
	 *
	 * @param value value indicating the client number
	 */
	public void updateClientNumber(final int value) {
		webSocketClient.send(ImmutableSetNumericValueMessage.builder()
				.data(value)
				.agentName(agentName)
				.type("SET_CLIENT_NUMBER")
				.build());
	}
}
