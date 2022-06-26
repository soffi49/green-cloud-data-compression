package com.gui.domain.nodes;

import static com.gui.utils.domain.StyleConstants.CLIENT_STYLE;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Agent node class representing the client
 */
public class ClientAgentNode extends AgentNode {

    public ClientAgentNode(String name) {
        super(name);
        this.style = CLIENT_STYLE;
    }

    @Override
    public Node addToGraph(Graph graph) {
        final Node node = super.addToGraph(graph);
        node.setAttribute("ui.hide","true");
        return node;
    }


}
