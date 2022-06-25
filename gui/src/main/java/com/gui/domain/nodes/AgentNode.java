package com.gui.domain.nodes;

import static com.gui.utils.StyleUtils.*;
import static com.gui.utils.domain.StyleConstants.LABEL_STYLE;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.Objects;

/**
 * Class represents abstract agent node
 */
public class AgentNode {

    protected String name;
    protected String style;

    /**
     * Class constructor
     *
     * @param name name of the agent
     */
    public AgentNode(String name) {
        this.name = name;
    }

    /**
     * Abstract method responsible for adding the node to the graph
     *
     * @param graph graph to which the node is to be added
     * @return added node
     */
    public Node addToGraph(final Graph graph){
        final Node node = graph.addNode(name);
        node.setAttribute("ui.label", node.getId());
        node.setAttribute("ui.class", concatenateStyles(List.of(LABEL_STYLE, style)));
        return node;
    }

    /**
     * Abstract method responsible for creating edges for given node
     *
     * @param graph graph for which the edges are to be created
     */
    public void createEdges(final Graph graph) {}

    /**
     * @return agent node name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name agent node name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentNode agentNode = (AgentNode) o;
        return name.equals(agentNode.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
