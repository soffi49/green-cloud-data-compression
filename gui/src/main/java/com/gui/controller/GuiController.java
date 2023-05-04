package com.gui.controller;

import java.io.Serializable;
import java.time.Instant;

import com.gui.agents.AbstractAgentNode;
import com.gui.event.domain.PowerShortageEvent;

/**
 * Controller for GUI
 */
public interface GuiController extends Runnable, Serializable {

	/**
	 * Method creates the GUI
	 */
	void run();

	/**
	 * Method adds next agent node to the graph
	 *
	 * @param agent node of the specified agent
	 */
	void addAgentNodeToGraph(final AbstractAgentNode agent);

	/**
	 * Method reports to the socket server time when the system was started
	 *
	 * @param time time of system start
	 */
	void reportSystemStartTime(final Instant time);

	/**
	 * Method remove agent node from the graph
	 *
	 * @param agentNode agent node that is to be removed
	 */
	void removeAgentNodeFromGraph(final AbstractAgentNode agentNode);

	/**
	 * Method updates the failed jobs number by given value
	 *
	 * @param value value to be added to the failed jobs number
	 */
	void updateFailedJobsCountByValue(final int value);

	/**
	 * Method updates the finished jobs number by given value
	 *
	 * @param value value to be added to the finished jobs number
	 */
	void updateFinishedJobsCountByValue(final int value);


	/**
	 * Method triggers the power shortage event in the Cloud Network for specified agent
	 *
	 * @param powerShortageEvent data for event triggering
	 * @param agentName          name of the agent for which the event is being triggered
	 */
	void triggerPowerShortageEvent(final PowerShortageEvent powerShortageEvent, final String agentName);

}
