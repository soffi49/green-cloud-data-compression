package com.gui.graph;

import com.mxgraph.view.mxGraph;

/**
 * Service which handles all graph styling operations
 */
public interface GraphStyleService {

    /**
     * Method assigns the stylesheet to the graph
     *
     * @param graph mxGraph to which the stylesheet is being assigned
     */
    void addStyleSheetToGraph(final mxGraph graph);

    /**
     * Method assigns new stylesheet to the given graph element
     *
     * @param element element for which the new stylesheet will be assigned
     * @param graph graph that is being modified
     * @param newStyle new stylesheet name
     */
    void changeGraphElementStylesheet(final Object element, final mxGraph graph, String newStyle);
}
