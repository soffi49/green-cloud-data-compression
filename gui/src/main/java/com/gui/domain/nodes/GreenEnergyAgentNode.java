package com.gui.domain.nodes;

import static com.gui.utils.GraphUtils.addAgentEdgeToGraph;
import static com.gui.utils.domain.StyleConstants.GREEN_ENERGY_STYLE;

import org.graphstream.graph.Graph;

/**
 * Agent node class representing the green energy source
 */
public class GreenEnergyAgentNode extends AgentNode {

    private String monitoringAgent;
    private String serverAgent;

    public GreenEnergyAgentNode(String name) {
        super(name);
        this.style = GREEN_ENERGY_STYLE;
    }

    @Override
    public void createEdges(Graph graph) {
        addAgentEdgeToGraph(graph, name, monitoringAgent);
    }

    public String getMonitoringAgent() {
        return monitoringAgent;
    }

    public void setMonitoringAgent(String monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    public String getServerAgent() {
        return serverAgent;
    }

    public void setServerAgent(String serverAgent) {
        this.serverAgent = serverAgent;
    }
}
