package com.gui.domain.nodes;

import static com.gui.utils.GraphUtils.addAgentEdgeToGraph;
import static com.gui.utils.domain.StyleConstants.CLOUD_NETWORK_STYLE;

import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent node class representing the cloud network
 */
public class CloudNetworkAgentNode extends AgentNode {

    private List<String> serverAgents;

    public CloudNetworkAgentNode(String name) {
        super(name);
        this.style = CLOUD_NETWORK_STYLE;
        this.serverAgents = new ArrayList<>();
    }

    @Override
    public void createEdges(Graph graph) {
        serverAgents.forEach(serverName -> addAgentEdgeToGraph(graph, name, serverName));
    }

    public List<String> getServerAgents() {
        return serverAgents;
    }

    public void setServerAgents(List<String> serverAgents) {
        this.serverAgents = serverAgents;
    }
}
