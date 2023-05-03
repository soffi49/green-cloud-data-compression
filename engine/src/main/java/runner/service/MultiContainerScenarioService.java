package runner.service;

import static runner.constants.EngineConstants.RUN_AGENT_DELAY;
import static runner.domain.ScenarioConfiguration.eventFilePath;
import static runner.domain.ScenarioConfiguration.scenarioFilePath;
import static runner.domain.enums.ContainerTypeEnum.CLIENTS_CONTAINER_ID;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

	private boolean mainHost = true;
	private int hostId;

	/**
	 * Service's constructor for the main host. Main host is responsible for running Main-Container
	 * which contains within itself Jade's RMA, SNIFFER, DFService.
	 */
	public MultiContainerScenarioService()
			throws StaleProxyException, ExecutionException, InterruptedException {
		super();
	}

	/**
	 * Service's constructor for the remote host. Creates an AgentContainer which contains
	 * all agents underlying to the CloudNetworkAgent corresponding to
	 * the given host id.
	 *
	 * @param hostId     number of the host id
	 * @param mainHostIp IP address of the main host
	 */
	public MultiContainerScenarioService(Integer hostId, String mainHostIp)
			throws ExecutionException, InterruptedException, StaleProxyException {
		super(hostId, mainHostIp);
		this.mainHost = false;
		this.hostId = hostId;
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

		if (hostId == CLIENTS_CONTAINER_ID.ordinal()) {
			if (eventFilePath.isPresent()) {
				var factory = new AgentControllerFactoryImpl(agentContainer);
				eventService.runScenarioEvents(factory);
			} else {
				runClients();
			}
		} else {
			List<AgentController> controllers = runCloudNetworkContainers(scenario, hostId);
			runAgents(controllers);
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

	private List<AgentController> runCloudNetworkContainers(final ScenarioStructureArgs scenario,
			final Integer hostId) {
		var cloudNetworkArgs = scenario.getCloudNetworkAgentsArgs();
		var serversArgs = scenario.getServerAgentsArgs();
		var sourcesArgs = scenario.getGreenEnergyAgentsArgs();
		var monitorsArgs = scenario.getMonitoringAgentsArgs();

		return addAgentsToContainer(cloudNetworkArgs.get(hostId - 1), scenario, serversArgs, sourcesArgs, monitorsArgs);
	}

	private List<AgentController> addAgentsToContainer(final CloudNetworkArgs cloudNetworkArgs,
			final ScenarioStructureArgs scenario, final List<ServerAgentArgs> serversArgs,
			final List<GreenEnergyAgentArgs> sourcesArgs, final List<MonitoringAgentArgs> monitorsArgs) {
		var factory = new AgentControllerFactoryImpl(agentContainer);
		var servers = serversArgs.stream()
				.filter(server -> server.getOwnerCloudNetwork().equals(cloudNetworkArgs.getName()))
				.toList();
		var sources = sourcesArgs.stream()
				.filter(source -> servers.stream().anyMatch(server -> server.getName().equals(source.getOwnerSever())))
				.toList();
		var monitors = monitorsArgs.stream()
				.filter(monitor -> sources.stream()
						.anyMatch(source -> source.getMonitoringAgent().equals(monitor.getName())))
				.toList();
		var controllers = new ArrayList<AgentController>();
		controllers.addAll(monitors.stream().map(m -> runAgentController(m, scenario, factory)).toList());
		controllers.addAll(sources.stream().map(s -> runAgentController(s, scenario, factory)).toList());
		controllers.addAll(servers.stream().map(s -> runAgentController(s, scenario, factory)).toList());
		controllers.add(runAgentController(cloudNetworkArgs, scenario, factory));
		return controllers;
	}
}
