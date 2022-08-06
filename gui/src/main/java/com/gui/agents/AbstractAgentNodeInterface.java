package com.gui.agents;

import java.util.Map;

import javax.swing.JPanel;

import com.gui.event.domain.AbstractEvent;
import com.gui.event.domain.EventTypeEnum;
import com.gui.graph.GraphService;

public interface AbstractAgentNodeInterface {

	/**
	 * Method retrieves the agent name
	 *
	 * @return agent name
	 */
	String getAgentName();

	/**
	 * Method responsible for adding the node to the graph
	 */
	void addToGraph(final GraphService graphService);

	/**
	 * Method responsible for creating edges for given node
	 */
	void createEdges();

	/**
	 * Method which based on the agent status creates the JPanel displaying all agent details
	 */
	void createInformationPanel();

	/**
	 * Method responsible for updating graph style based on the internal state of agent node
	 */
	void updateGraphUI();

	/**
	 * Method used to initialize labels map for given agent node
	 */
	void initializeLabelsMap();

	/**
	 * Method is responsible for adding new event to the agent event stack
	 *
	 * @param event event to be added to the stack
	 */
	void addEventToStack(final AbstractEvent event);

	/**
	 * Method removes the first event from the event stack
	 *
	 * @return removed event
	 */
	AbstractEvent removeEventFromStack();

	/**
	 * Method retrieves the map containing all available agent events with their current statuses
	 *
	 * @return available events for the agent
	 */
	Map<EventTypeEnum, Boolean> getAgentEvents();

	/**
	 * Method retrieves the JPanel containing agent details
	 *
	 * @return JPanel with agent details
	 */
	JPanel getAgentDetailsPanel();
}
