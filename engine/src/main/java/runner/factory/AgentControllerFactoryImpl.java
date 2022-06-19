package runner.factory;

import com.gui.domain.*;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.domain.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentControllerFactoryImpl implements AgentControllerFactory {

    private static AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    private final ContainerController containerController;

    public AgentControllerFactoryImpl(ContainerController containerController) {
        this.containerController = containerController;
    }

    @Override
    public AgentController createAgentController(AgentArgs agentArgs)
            throws StaleProxyException {

        if (agentArgs instanceof ClientAgentArgs clientAgent) {
            final String startDate = formatDate(OffsetDateTime.now().plusHours(Long.parseLong(clientAgent.getStart())));
            final String endDate = formatDate(OffsetDateTime.now().plusHours(Long.parseLong(clientAgent.getEnd())));

            return containerController.createNewAgent(clientAgent.getName(), "agents.client.ClientAgent",
                                                      new Object[]{startDate, endDate, clientAgent.getPower(), clientAgent.getJobId()});
        } else if (agentArgs instanceof ServerAgentArgs serverAgent) {
            return containerController.createNewAgent(serverAgent.getName(), "agents.server.ServerAgent",
                                                      new Object[]{serverAgent.getOwnerCloudNetwork(), serverAgent.getPrice(), serverAgent.getMaximumCapacity()});
        } else if (agentArgs instanceof CloudNetworkArgs cloudNetworkAgent) {
            return containerController.createNewAgent(cloudNetworkAgent.getName(),
                                                      "agents.cloudnetwork.CloudNetworkAgent", new Object[]{});
        } else if (agentArgs instanceof GreenEnergyAgentArgs greenEnergyAgent) {
            return containerController.createNewAgent(greenEnergyAgent.getName(),
                                                      "agents.greenenergy.GreenEnergyAgent",
                                                      new Object[]{greenEnergyAgent.getMonitoringAgent(),
                                                              greenEnergyAgent.getOwnerSever(),
                                                              greenEnergyAgent.getPricePerPowerUnit(),
                                                              greenEnergyAgent.getMaximumCapacity(),
                                                              greenEnergyAgent.getLatitude(),
                                                              greenEnergyAgent.getLongitude()});
        } else if (agentArgs instanceof MonitoringAgentArgs monitoringAgent) {
            return containerController.createNewAgent(monitoringAgent.getName(),
                                                      "agents.monitoring.MonitoringAgent",
                                                      new Object[]{});
        }
        return null;
    }

    @Override
    public AgentNode createAgentNode(AgentArgs agentArgs, ScenarioArgs scenarioArgs) {
        if (agentArgs instanceof ClientAgentArgs clientArgs) {
            return new ClientAgentNode(clientArgs.getName(), ID_GENERATOR.getAndIncrement());
        }
        if (agentArgs instanceof CloudNetworkArgs cloudNetworkArgs) {
            final CloudNetworkAgentNode cloudNetworkAgentNode = new CloudNetworkAgentNode(cloudNetworkArgs.getName());
            cloudNetworkAgentNode.setServerAgents(scenarioArgs.getServerAgentsArgs().stream()
                                                          .filter(serverArgs -> serverArgs.getOwnerCloudNetwork().equals(cloudNetworkArgs.getName()))
                                                          .map(ImmutableServerAgentArgs::getName)
                                                          .toList());
            return cloudNetworkAgentNode;
        }
        if (agentArgs instanceof GreenEnergyAgentArgs greenEnergyAgentArgs) {
            final GreenEnergyAgentNode greenEnergyAgentNode = new GreenEnergyAgentNode(greenEnergyAgentArgs.getName());
            greenEnergyAgentNode.setMonitoringAgent(greenEnergyAgentArgs.getMonitoringAgent());
            greenEnergyAgentNode.setServerAgent(greenEnergyAgentArgs.getOwnerSever());
            final Coordinates coordinates = new Coordinates(Double.parseDouble(greenEnergyAgentArgs.getLatitude()),
                                                            Double.parseDouble(greenEnergyAgentArgs.getLongitude()));
            greenEnergyAgentNode.setCoordinates(coordinates);
            return greenEnergyAgentNode;
        }
        if (agentArgs instanceof MonitoringAgentArgs monitoringAgentArgs) {
            final MonitoringAgentNode monitoringAgentNode = new MonitoringAgentNode(monitoringAgentArgs.getName());
            final ImmutableGreenEnergyAgentArgs ownerGreenSource = scenarioArgs.getGreenEnergyAgentsArgs().stream()
                    .filter(greenSourceArgs -> greenSourceArgs.getMonitoringAgent().equals(monitoringAgentArgs.getName()))
                    .findFirst()
                    .orElse(null);
            monitoringAgentNode.setGreenEnergyAgent(ownerGreenSource.getName());
            monitoringAgentNode.setCoordinates(new Coordinates(Double.parseDouble(ownerGreenSource.getLongitude()) + 10,
                                                               Double.parseDouble(ownerGreenSource.getLatitude())));
            return monitoringAgentNode;
        }
        if (agentArgs instanceof ServerAgentArgs serverAgentArgs) {
            final ServerAgentNode serverAgentNode = new ServerAgentNode(serverAgentArgs.getName());
            final List<ImmutableGreenEnergyAgentArgs> ownedGreenSources = scenarioArgs.getGreenEnergyAgentsArgs().stream()
                    .filter(greenEnergyArgs -> greenEnergyArgs.getOwnerSever().equals(serverAgentArgs.getName()))
                    .toList();
            serverAgentNode.setCloudNetworkAgent(serverAgentArgs.getOwnerCloudNetwork());
            serverAgentNode.setGreenEnergyAgents(ownedGreenSources.stream().map(ImmutableGreenEnergyAgentArgs::getName).toList());
            return serverAgentNode;
        }
        return null;
    }

    private String formatDate(final OffsetDateTime date) {
        final String dateFormat = "dd/MM/yyyy HH:mm";
        return date.format(DateTimeFormatter.ofPattern(dateFormat));
    }
}
