package com.gui.graph;

import com.gui.agents.AbstractAgentNode;
import com.mxgraph.swing.mxGraphComponent;

import java.util.List;

/**
 * Service which handles all graph operations
 */
public interface GraphService {

    /**
     * Method creates a graph with applied default configuration
     *
     * @return mxGraphComponent
     */
    mxGraphComponent createGraphComponent();

    /**
     * Method creates a new vertex based on agent node and adds it to the graph
     *
     * @param node agent node to be added to the graph
     */
    void createAndAddNodeToGraph(final AbstractAgentNode node);


    /**
     * Method removes a vertex from the graph
     *
     * @param node agent node to removed from the graph
     */
    void removeNodeFromGraph(final AbstractAgentNode node);

    /**
     * Method updates a stylesheet for the vertex representing given node
     *
     * @param node      agent node for which the style should change
     * @param styleName name of the new stylesheet to be applied
     */
    void updateNodeStyle(final String node, final String styleName);

    /**
     * Method creates a new edge and adds it to the graph
     *
     * @param nodeSource first node name (source node in case of directed edge)
     * @param nodeTarget second node name (target node in case of directed edge)
     * @param isDirected flag indicating if the edge is directed
     */
    void createAndAddEdgeToGraph(final String nodeSource, final String nodeTarget, final boolean isDirected);

    /**
     * Method updates a stylesheet for the edge between given nodes
     *
     * @param sourceNode source node name
     * @param targetNode target node name
     * @param isDirected flag indicating if the edge is directed
     * @param styleName  name of the new stylesheet to be applied
     */
    void updateEdgeStyle(final String sourceNode, final String targetNode, final boolean isDirected, final String styleName);

    /**
     * Method displays the message arrows between indicated nodes
     *
     * @param sourceNode  node for which the message is sent
     * @param targetNodes list of nodes which receive the message
     */
    void displayMessageEdges(final String sourceNode, final List<String> targetNodes);

    /**
     * Method updates the graph layout
     */
    void updateGraphLayout();
}
