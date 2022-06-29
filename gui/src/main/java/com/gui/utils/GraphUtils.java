package com.gui.utils;

import static com.gui.utils.domain.StyleConstants.EDGE_HIDDEN_MESSAGE_STYLE;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import java.util.List;

/**
 * Class provides set of utilities connected with graph methods which are used by multiple classes
 */
public class GraphUtils {

    /**
     * Method creates edge based on given agents
     *
     * @param graph  graph to which the edge is added
     * @param agent1 name of the first agent
     * @param agent2 name of the second agent
     */
    public static void addAgentEdgeToGraph(final Graph graph,
                                           final List<Edge> nodeEdges,
                                           final String agent1,
                                           final String agent2) {
        final String edgeName = String.join("_", agent1, agent2);
        final Edge edge = graph.addEdge(edgeName, agent1, agent2, true);
        edge.setAttribute("ui.class", EDGE_HIDDEN_MESSAGE_STYLE);
        nodeEdges.add(edge);
    }
}
