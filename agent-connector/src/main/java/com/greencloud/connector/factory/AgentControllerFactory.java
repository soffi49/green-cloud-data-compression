package com.greencloud.connector.factory;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.greencloud.commons.args.agent.AgentArgs;
import org.greencloud.commons.args.scenario.ScenarioStructureArgs;
import org.greencloud.gui.agents.egcs.EGCSNode;

import com.database.knowledge.timescale.TimescaleDatabase;

import jade.core.AID;
import jade.wrapper.AgentController;

/**
 * Factory used to create and run agent controllers
 */
public interface AgentControllerFactory {

	/**
	 * Method creates the agent controllers
	 *
	 * @param agentArgs agent arguments
	 * @return AgentController that can be started
	 */
	AgentController createAgentController(AgentArgs agentArgs);

	/**
	 * Method creates the agent controllers
	 *
	 * @param agentArgs agent arguments
	 * @param agentNode GUI agent node
	 * @return AgentController that can be started
	 */
	AgentController createAgentController(AgentArgs agentArgs, EGCSNode<?, ?> agentNode);

	/**
	 * Method creates the agent controllers
	 *
	 * @param agentArgs agent arguments
	 * @param scenario  which has to be passed to managing agent
	 * @return AgentController that can be started
	 */
	AgentController createAgentController(AgentArgs agentArgs, ScenarioStructureArgs scenario);

	/**
	 * Method creates the agent controllers
	 *
	 * @param agentArgs agent arguments
	 * @param className name of the class from which the agent is to be created
	 * @return AgentController that can be started
	 */
	AgentController createAgentController(AgentArgs agentArgs, final String className);

	/**
	 * Method creates the agent controllers
	 *
	 * @param agentArgs     agent arguments
	 * @param scenario      which has to be passed to managing agent
	 * @param isInformer    flag indicating if the created controller should send starting information to managing agent
	 * @param managingAgent AID of managing agent with which the given agent should communicate
	 * @return AgentController that can be started
	 */
	AgentController createAgentController(AgentArgs agentArgs,
			ScenarioStructureArgs scenario,
			boolean isInformer,
			AID managingAgent);

	/**
	 * Method runs the agent controllers
	 *
	 * @param controllers controllers that are to be run
	 */
	void runAgentControllers(final List<AgentController> controllers, final long agentRunDelay);

	/**
	 * Method runs single agent controller
	 *
	 * @param controller controller that is to be run
	 */
	void runAgentController(final AgentController controller, final long agentRunDelay);

	/**
	 * Method used handle runnable execution termination
	 *
	 * @param executorService executor service that runs given process
	 */
	void shutdownAndAwaitTermination(final ExecutorService executorService);

	/**
	 * Method returns database instance
	 *
	 * @return TimeScale Database
	 */
	TimescaleDatabase getDatabase();
}
