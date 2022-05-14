package runner.factory;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import runner.domain.AgentArgs;

public interface AgentControllerFactory {

    AgentController createAgentController(AgentArgs agentArgs) throws StaleProxyException;

}
