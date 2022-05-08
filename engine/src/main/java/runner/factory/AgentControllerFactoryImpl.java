package runner.factory;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.domain.AgentArgs;
import runner.domain.ClientAgentArgs;
import runner.domain.CloudNetworkArgs;
import runner.domain.ServerAgentArgs;

public class AgentControllerFactoryImpl implements AgentControllerFactory {

    private final ContainerController containerController;

    public AgentControllerFactoryImpl(ContainerController containerController) {
        this.containerController = containerController;
    }

    @Override
    public AgentController createAgentController(AgentArgs agentArgs)
        throws StaleProxyException {

        if (agentArgs instanceof ClientAgentArgs clientAgent) {
            return containerController.createNewAgent(clientAgent.getName(), "agents.client.ClientAgent",
                new Object[]{clientAgent.getStartDate(), clientAgent.getEndDate(), clientAgent.getPower()});
        } else if (agentArgs instanceof ServerAgentArgs serverAgent) {
            return containerController.createNewAgent(serverAgent.getName(), "agents.server.ServerAgent",
                new Object[]{serverAgent.getOwnerCloudNetwork(), serverAgent.getPrice(), serverAgent.getPower()});
        } else if (agentArgs instanceof CloudNetworkArgs cloudNetworkAgent) {
            return containerController.createNewAgent(cloudNetworkAgent.getName(),
                "agents.cloudnetwork.CloudNetworkAgent", new Object[]{});
        }

        return null;
    }
}
