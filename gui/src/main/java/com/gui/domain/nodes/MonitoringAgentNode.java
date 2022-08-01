package com.gui.domain.nodes;

/**
 * Agent node class representing the monitoring agent
 */
public class MonitoringAgentNode extends AgentNode {

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
        graphService.createAndAddEdgeToGraph(name, greenEnergyAgent, true);
    }

}
