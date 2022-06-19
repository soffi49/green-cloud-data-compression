package com.gui;

import com.gui.controller.GraphController;
import org.graphstream.graph.Graph;

public class AgentsGui {

    public static void main(String[] args) {
        GraphController graphController = new GraphController();
        Graph graph = graphController.getGraph();
        graph.display();
    }
}