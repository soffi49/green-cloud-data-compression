package com.gui.controller;

import com.gui.domain.nodes.AgentNode;

/**
 * Controller for GUI
 */
public interface GUIController {

    /**
     * Method creates the GUI
     */
    void createGUI();

    /**
     * Method adds next agent node to the graph
     *
     * @param agent node of the specified agent
     */
    void addAgentNodeToGraph(final AgentNode agent);

    /**
     * Method remove agent node from the graph
     *
     * @param agentName name of the agent node that is to be removed
     */
    void removeAgentNodeToGraph(final String agentName);

    /**
     * Method updates the clients number by given value
     *
     * @param value value to be added to the clients number
     */
    void updateClientsCountByValue(final int value);

    /**
     * Method updates the active jobs number by given value
     *
     * @param value value to be added to the active jobs number
     */
    void updateActiveJobsCountByValue(final int value);

    /**
     * Method updates the all jobs number by given value
     *
     * @param value value to be added to the all jobs number
     */
    void updateAllJobsCountByValue(final int value);

    /**
     * Method adds new information to the latest news panel
     *
     * @param information information that is to be added
     */
    void addNewInformation(final String information);
}
