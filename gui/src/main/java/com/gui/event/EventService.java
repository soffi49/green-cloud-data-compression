package com.gui.event;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;

import com.gui.agents.domain.AgentNode;

/**
 * Service which provides the methods connected which events handling
 */
public interface EventService {

	/**
	 * Action that is invoked when the power shortage button is pressed
	 *
	 * @param maximumPowerInput input field for maximum power during power shortage given by the administrator
	 * @param agentNode         affected by the power shortage agent
	 * @param eventButton       button that was pressed
	 */
	void causePowerShortage(final JFormattedTextField maximumPowerInput, final AgentNode agentNode,
			final JButton eventButton);

}
