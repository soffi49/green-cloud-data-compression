package runner.service;

import static runner.domain.ScenarioConfiguration.eventFilePath;
import static runner.domain.ScenarioConfiguration.scenarioFilePath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;

import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import runner.factory.AgentControllerFactory;
import runner.factory.AgentControllerFactoryImpl;

/**
 * Service used in running the scenarios on a single physical host.
 */
public class SingleContainerScenarioService extends AbstractScenarioService implements Runnable {

	private static final List<AgentController> AGENTS_TO_RUN = new ArrayList<>();
	private final AgentControllerFactory factory;

	/**
	 * Runs single scenario service with a single controller on a single physical host.
	 */
	public SingleContainerScenarioService()
			throws StaleProxyException, ExecutionException, InterruptedException {
		super();
		this.factory = new AgentControllerFactoryImpl(mainContainer);
	}

	@Override
	public void run() {
		final File scenarioStructureFile = readFile(scenarioFilePath);
		scenario = parseScenarioStructure(scenarioStructureFile);
		if (Objects.nonNull(scenario.getAgentsArgs())) {
			createAgents(List.of(scenario.getManagingAgentArgs()), scenario);
			createAgents(List.of(scenario.getSchedulerAgentArgs()), scenario);
			createAgents(scenario.getMonitoringAgentsArgs(), scenario);
			createAgents(scenario.getGreenEnergyAgentsArgs(), scenario);
			createAgents(scenario.getServerAgentsArgs(), scenario);
			createAgents(scenario.getCloudNetworkAgentsArgs(), scenario);
		}
		updateSystemStartTime();
		runAgents(AGENTS_TO_RUN);

		if (eventFilePath.isEmpty()) {
			runClientAgents(factory);
		} else {
			eventService.runScenarioEvents(factory);
		}
	}

	private void createAgents(List<?> agentArgsList, ScenarioStructureArgs scenario) {
		agentArgsList.forEach(agentArgs -> {
			var args = (AgentArgs) agentArgs;
			AGENTS_TO_RUN.add(runAgentController(args, scenario, factory));
		});
	}
}
