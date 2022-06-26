package com.gui.domain.nodes;


import static com.gui.utils.GraphUtils.addAgentEdgeToGraph;
import static com.gui.utils.domain.StyleConstants.SERVER_STYLE;

import org.graphstream.graph.Graph;

import java.util.List;

/**
 * Agent node class representing the server
 */
public class ServerAgentNode extends AgentNode {
    private String cloudNetworkAgent;
    private List<String> greenEnergyAgents;

    public ServerAgentNode(String name) {
        super(name);
        this.style = SERVER_STYLE;
    }

    @Override
    public void createEdges(Graph graph) {
        greenEnergyAgents.forEach(greenEnergyName -> addAgentEdgeToGraph(graph, name, greenEnergyName));
    }

    public String getCloudNetworkAgent() {
        return cloudNetworkAgent;
    }

    public void setCloudNetworkAgent(String cloudNetworkAgent) {
        this.cloudNetworkAgent = cloudNetworkAgent;
    }

    public List<String> getGreenEnergyAgents() {
        return greenEnergyAgents;
    }

    public void setGreenEnergyAgents(List<String> greenEnergyAgents) {
        this.greenEnergyAgents = greenEnergyAgents;
    }
}
