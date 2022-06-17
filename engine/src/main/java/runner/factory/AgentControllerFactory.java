package runner.factory;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import runner.domain.AgentArgs;

/**
 * Factory used to create agent controllers
 */
public interface AgentControllerFactory {

    /**
     * Method creates the agent controllers
     *
     * @param agentArgs agent arguments
     * @return AgentController that can be started
     * @throws StaleProxyException
     */
    AgentController createAgentController(AgentArgs agentArgs) throws StaleProxyException;

}
