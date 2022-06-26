package com.gui.utils;

import org.graphstream.graph.Graph;

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
                                           final String agent1,
                                           final String agent2) {
        final String edgeName = String.join("_", agent1, agent2);
        graph.addEdge(edgeName, agent1, agent2);
    }
}
