package runner.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static runner.constants.EngineConstants.RUN_AGENT_DELAY;
import static runner.domain.EngineConfiguration.containerId;
import static runner.domain.EngineConfiguration.locationId;
import static runner.domain.EngineConfiguration.mainHost;
import static runner.domain.EngineConfiguration.newPlatform;
import static runner.domain.ScenarioConfiguration.eventFilePath;
import static runner.domain.ScenarioConfiguration.scenarioFilePath;
import static runner.domain.enums.ContainerTypeEnum.CLIENTS_CONTAINER_ID;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.args.agent.cloudnetwork.CloudNetworkArgs;
import com.greencloud.commons.args.agent.greenenergy.GreenEnergyAgentArgs;
import com.greencloud.commons.args.agent.monitoring.MonitoringAgentArgs;
import com.greencloud.commons.args.agent.server.ServerAgentArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import runner.factory.AgentControllerFactory;
import runner.factory.AgentControllerFactoryImpl;

/**
 * Scenario service responsible for running Green Cloud dispersed among multiple hosts.
 * Each host is responsible for running for single Cloud Network. Additionally, to that
 */
public class MultiContainerScenarioService extends AbstractScenarioService implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(MultiContainerScenarioService.class);

	/**
	 * Service's constructor for the main host. Main host is responsible for running Main-Container
	 * which contains within itself Jade's RMA, SNIFFER, DFService.
	 */
	public MultiContainerScenarioService()
			throws StaleProxyException, ExecutionException, InterruptedException {
		super();
	}

	/**
	 * Runs AgentContainer. For example host with id = 1 would run all agents for first CNA declared
	 * in the XML scenario document. A special case is host with id = 0, such host is responsible
	 * for generating Client Agents.
	 */
	@Override
	public void run() {
		final File scenarioFile = readFile(scenarioFilePath);
		scenario = parseScenarioStructure(scenarioFile);
		updateSystemStartTime();

		if (mainHost) {
			runCommonAgentContainers(scenario);
			return;
		}

		if (nonNull(locationId) && locationId.contains(CLIENTS_CONTAINER_ID.getName())) {
			if (eventFilePath.isPresent()) {
				var factory = new AgentControllerFactoryImpl(agentContainer);
				eventService.runScenarioEvents(factory);
			} else {
				runClients();
			}
		} else {
			List<AgentController> controllers = runCloudNetworkContainers(scenario);

			if (controllers.isEmpty()) {
				logger.info("No agents to be run! Make sure that you passed a correct configuration.");
			} else {
				runAgents(controllers);
			}
		}
	}

	private void runClients() {
		final AgentControllerFactory clientFactory = new AgentControllerFactoryImpl(agentContainer);
		runClientAgents(clientFactory);
	}

	private void runCommonAgentContainers(final ScenarioStructureArgs scenario) {
		final AgentControllerFactory factory = new AgentControllerFactoryImpl(mainContainer);
		final AgentController schedulerController = runAgentController(scenario.getSchedulerAgentArgs(),
				scenario, factory);
		final AgentController managingAgentController = runAgentController(scenario.getManagingAgentArgs(),
				scenario, factory);

		runAgent(schedulerController, RUN_AGENT_DELAY);
		runAgent(managingAgentController, RUN_AGENT_DELAY);
	}

	private List<AgentController> runCloudNetworkContainers(final ScenarioStructureArgs scenario) {
		var factory = new AgentControllerFactoryImpl(agentContainer);

		var clouds = selectCloudNetworksForContainers();
		var servers = selectServersForContainer(clouds);
		var sources = selectGreenSourcesForContainer(servers);
		var monitors = selectMonitoringForContainer(sources);

		var controllers = new ArrayList<AgentController>();
		controllers.addAll(monitors.stream().map(m -> runAgentController(m, scenario, factory)).toList());
		controllers.addAll(sources.stream().map(s -> runAgentController(s, scenario, factory)).toList());
		controllers.addAll(servers.stream().map(s -> runAgentController(s, scenario, factory)).toList());

		if (newPlatform || isNull(containerId)) {
			controllers.addAll(clouds.stream().map(cloud -> runAgentController(cloud, scenario, factory)).toList());
		}
		return controllers;
	}

	private List<CloudNetworkArgs> selectCloudNetworksForContainers() {
		final List<CloudNetworkArgs> cloudNetworkArgs = scenario.getCloudNetworkAgentsArgs();

		return cloudNetworkArgs.stream()
				.filter(cnaArgs -> Objects.equals(cnaArgs.getLocationId(), locationId))
				.toList();
	}

	private List<ServerAgentArgs> selectServersForContainer(final List<CloudNetworkArgs> cloudNetworkArgs) {
		final List<ServerAgentArgs> serverAgentsArgs = scenario.getServerAgentsArgs();

		return serverAgentsArgs.stream()
				.filter(serverArgs -> cloudNetworkArgs.stream().map(AgentArgs::getName).toList()
						.contains(serverArgs.getOwnerCloudNetwork()))
				.filter(serverArgs -> Objects.equals(serverArgs.getContainerId(), containerId))
				.toList();
	}

	private List<GreenEnergyAgentArgs> selectGreenSourcesForContainer(final List<ServerAgentArgs> serverAgentArgs) {
		final List<GreenEnergyAgentArgs> greenEnergyAgentArgs = scenario.getGreenEnergyAgentsArgs();

		return greenEnergyAgentArgs.stream()
				.filter(sourceArgs ->
						serverAgentArgs.stream().map(AgentArgs::getName).toList().contains(sourceArgs.getOwnerSever()))
				.toList();
	}

	private List<MonitoringAgentArgs> selectMonitoringForContainer(
			final List<GreenEnergyAgentArgs> greenEnergyAgentArgs) {
		final List<MonitoringAgentArgs> monitoringAgentArgs = scenario.getMonitoringAgentsArgs();

		return monitoringAgentArgs.stream()
				.filter(monitorArgs -> greenEnergyAgentArgs.stream().map(GreenEnergyAgentArgs::getMonitoringAgent)
						.toList()
						.contains(monitorArgs.getName()))
				.toList();
	}
}
