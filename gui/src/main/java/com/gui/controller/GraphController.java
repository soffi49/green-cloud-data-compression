package com.gui.controller;

import com.gui.domain.AgentNode;
import com.gui.domain.GreenEnergyAgentNode;
import com.gui.domain.ServerAgentNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphController {

    private static final String STYLE_FILE = "url(graphStyle.css)";

    private final Graph graph;
    private final List<AgentNode> graphNodes;

    public GraphController() {
        System.setProperty("org.graphstream.ui", "swing");
        this.graph = new MultiGraph("Cloud Network");
        this.graph.setAttribute("ui.stylesheet", STYLE_FILE);
        this.graphNodes = new ArrayList<>();
        graph.display();
    }

    public void addAgentNodeToGraph(final AgentNode agent) {
        graphNodes.add(agent);
        agent.addToGraph(graph);
    }

    public void addAgentConnectionToGraph(final AgentNode agent1,
                                          final AgentNode agent2) {
        final String edgeName = String.join("_", agent1.getName(), agent2.getName());
        graph.addEdge(edgeName, agent1.getName(), agent2.getName());
    }

    public Graph getGraph() {
        return graph;
    }
}
