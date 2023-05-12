package com.gui.agents;

import static com.gui.websocket.WebSocketConnections.getAgentsWebSocket;

import java.io.Serializable;

import com.greencloud.commons.args.agent.monitoring.ImmutableMonitoringNodeArgs;
import com.gui.message.ImmutableRegisterAgentMessage;

/**
 * Agent node class representing the monitoring agent
 */
public class MonitoringAgentNode extends AbstractAgentNode implements Serializable {

	private final String greenEnergyAgent;

	/**
	 * Monitoring node constructor
	 *
	 * @param name             node name
	 * @param greenEnergyAgent owner green energy agent
	 */
	public MonitoringAgentNode(String name, String greenEnergyAgent) {
		super(name);
		this.greenEnergyAgent = greenEnergyAgent;
	}

	@Override
	public void addToGraph() {
		getAgentsWebSocket().send(ImmutableRegisterAgentMessage.builder()
				.agentType("MONITORING")
				.data(ImmutableMonitoringNodeArgs.builder()
						.name(agentName)
						.greenEnergyAgent(greenEnergyAgent)
						.build())
				.build());
	}
}
