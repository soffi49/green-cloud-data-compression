package com.greencloud.factory;

import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;
import com.gui.agents.AbstractAgentNode;

/**
 * Factory used to create agent nodes
 */
public interface AgentNodeFactory {

	/**
	 * Method creates the graph node based on the scenario arguments
	 *
	 * @param agentArgs    current agent arguments
	 * @param scenarioArgs scenario arguments
	 */
	AbstractAgentNode createAgentNode(final AgentArgs agentArgs, final ScenarioStructureArgs scenarioArgs);
}
