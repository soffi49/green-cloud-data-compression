package com.gui.event;

import java.awt.Component;

import com.gui.agents.domain.AgentNode;

/**
 * Service responsible for incorporating the event handling functionalities into GUI
 */
public interface EventGUIService {

	/**
	 * Method creates the panel with event buttons and handlers for given agent
	 *
	 * @param agentNode agent for which the event panel is generated
	 * @return JPanel being an event panel
	 */
	Component createEventPanelForAgent(final AgentNode agentNode);
}
