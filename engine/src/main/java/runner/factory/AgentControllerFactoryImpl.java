package runner.factory;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.domain.*;

import java.util.concurrent.TimeUnit;

public class AgentControllerFactoryImpl implements AgentControllerFactory {

    private final ContainerController containerController;

    public AgentControllerFactoryImpl(ContainerController containerController) {
        this.containerController = containerController;
    }

    @Override
    public AgentController createAgentController(AgentArgs agentArgs)
        throws StaleProxyException {

        if (agentArgs instanceof ClientAgentArgs clientAgent) {
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return containerController.createNewAgent(clientAgent.getName(), "agents.client.ClientAgent",
                new Object[]{clientAgent.getStartDate(), clientAgent.getEndDate(), clientAgent.getPower(), clientAgent.getJobId()});
        } else if (agentArgs instanceof ServerAgentArgs serverAgent) {
            return containerController.createNewAgent(serverAgent.getName(), "agents.server.ServerAgent",
                new Object[]{serverAgent.getOwnerCloudNetwork(), serverAgent.getPrice(), serverAgent.getMaximumCapacity()});
        } else if (agentArgs instanceof CloudNetworkArgs cloudNetworkAgent) {
            return containerController.createNewAgent(cloudNetworkAgent.getName(),
                "agents.cloudnetwork.CloudNetworkAgent", new Object[]{});
        }
        else if(agentArgs instanceof GreenEnergyAgentArgs greenEnergyAgent){
            return containerController.createNewAgent(greenEnergyAgent.getName(),
                    "agents.greenenergy.GreenEnergyAgent",
                    new Object[]{greenEnergyAgent.getMonitoringAgent(),
                            greenEnergyAgent.getOwnerSever(),
                            greenEnergyAgent.getPricePerPowerUnit(),
                            greenEnergyAgent.getMaximumCapacity(),
                            greenEnergyAgent.getLatitude(),
                            greenEnergyAgent.getLongitude()});
        }
        else if(agentArgs instanceof MonitoringAgentArgs monitoringAgent){
            return containerController.createNewAgent(monitoringAgent.getName(),
                    "agents.monitoring.MonitoringAgent",
                    new Object[]{});
        }
        return null;
    }
}
