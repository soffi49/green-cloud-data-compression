package com.gui.agents;

/**
 * Agent node class representing the monitoring agent
 */
public class MonitoringAgentNode extends AbstractAgentNode {

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
	public void createEdges() {
		graphService.createAndAddEdgeToGraph(agentName, greenEnergyAgent, true);
	}

	@Override
	public void updateGraphUI() {
	}

	@Override
	public void initializeLabelsMap() {
	}

}
