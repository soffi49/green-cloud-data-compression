package runner.service;

import static com.greencloud.application.utils.TimeUtils.setSystemStartTime;
import static com.greencloud.commons.args.agent.client.ClientTimeType.REAL_TIME;
import static com.greencloud.factory.constants.AgentControllerConstants.RUN_CLIENT_AGENT_DELAY;
import static jade.core.Runtime.instance;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static runner.constants.EngineConstants.MTP_MULTI_MESSAGE;
import static runner.configuration.EngineConfiguration.containerId;
import static runner.configuration.EngineConfiguration.databaseHostIp;
import static runner.configuration.EngineConfiguration.jadeInterPort;
import static runner.configuration.EngineConfiguration.jadeIntraPort;
import static runner.configuration.EngineConfiguration.localHostIp;
import static runner.configuration.EngineConfiguration.locationId;
import static runner.configuration.EngineConfiguration.mainHost;
import static runner.configuration.EngineConfiguration.mainHostIp;
import static runner.configuration.EngineConfiguration.newPlatform;
import static runner.configuration.EngineConfiguration.platformId;
import static runner.configuration.EngineConfiguration.runJadeGUI;
import static runner.configuration.EngineConfiguration.runJadeSniffer;
import static runner.configuration.EngineConfiguration.websocketAddress;
import static runner.configuration.ScenarioConfiguration.clientNumber;
import static runner.configuration.ScenarioConfiguration.eventFilePath;
import static runner.configuration.ScenarioConfiguration.maxDeadline;
import static runner.configuration.ScenarioConfiguration.maxEndTime;
import static runner.configuration.ScenarioConfiguration.maxJobPower;
import static runner.configuration.ScenarioConfiguration.maxStartTime;
import static runner.configuration.ScenarioConfiguration.minJobPower;
import static runner.configuration.ScenarioConfiguration.minStartTime;
import static runner.configuration.ScenarioConfiguration.scenarioFilePath;
import static runner.configuration.enums.ContainerTypeEnum.CLIENTS_CONTAINER_ID;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

import com.database.knowledge.timescale.TimescaleDatabase;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.greencloud.commons.args.agent.client.ClientAgentArgs;
import com.greencloud.commons.args.agent.managing.ManagingAgentArgs;
import com.greencloud.commons.exception.InvalidScenarioException;
import com.greencloud.commons.exception.JadeContainerException;
import com.greencloud.commons.exception.JadeControllerException;
import com.greencloud.commons.scenario.ScenarioEventsArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;
import com.greencloud.factory.AgentControllerFactory;
import com.greencloud.factory.AgentFactory;
import com.greencloud.factory.AgentFactoryImpl;
import com.greencloud.factory.AgentNodeFactoryImpl;
import com.gui.controller.GuiController;
import com.gui.controller.GuiControllerImpl;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Abstract class serving as common base to Single and Multi Scenario Services.
 * It handles creation of Main and Agent's Containers as well as Agent's Controllers.
 * It is also responsible for running Agent's and Agent's clients.
 */
public abstract class AbstractScenarioService {

	protected static final XmlMapper xmlMapper = new XmlMapper();
	protected static final ExecutorService executorService = Executors.newCachedThreadPool();
	protected final ScenarioEventService eventService;
	protected final GuiController guiController;
	protected final TimescaleDatabase timescaleDatabase;
	protected final Runtime jadeRuntime;
	protected final ContainerController mainContainer;
	protected final ContainerController agentContainer;

	protected AgentFactory agentFactory;
	protected AgentControllerFactory factory;
	protected ScenarioStructureArgs scenario;

	/**
	 * Constructor called by {@link MultiContainerScenarioService} and {@link SingleContainerScenarioService}
	 * Launches gui and the main controller.
	 * In case of MultiContainer case runs environment only for the main host.
	 */
	protected AbstractScenarioService()
			throws ExecutionException, InterruptedException, StaleProxyException {
		this.agentFactory = new AgentFactoryImpl();
		this.guiController = new GuiControllerImpl(websocketAddress);
		this.eventService = new ScenarioEventService(this);
		this.jadeRuntime = instance();
		this.timescaleDatabase = new TimescaleDatabase(databaseHostIp);

		if (mainHost) {
			timescaleDatabase.initDatabase();
		}

		executorService.execute(guiController);
		mainContainer = (newPlatform || mainHost) ? runMainContainer() : null;
		agentContainer = mainHost ? null : runAgentsContainer();

		if (runJadeGUI && (mainHost || newPlatform)) {
			runJadeGui();
		}
	}

	protected File readFile(final String filePath) {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
			final File scenarioTempFile = File.createTempFile("test", ".txt");
			copyInputStreamToFile(inputStream, scenarioTempFile);
			return scenarioTempFile;
		} catch (IOException | NullPointerException e) {
			throw new InvalidScenarioException("Invalid scenario file name.", e);
		}
	}

	protected ScenarioStructureArgs parseScenarioStructure(final File scenarioStructureFile) {
		try {
			return xmlMapper.readValue(scenarioStructureFile, ScenarioStructureArgs.class);
		} catch (IOException e) {
			throw new InvalidScenarioException(
					format("Failed to parse scenario structure file \"%s\"", scenarioFilePath), e);
		}
	}

	protected ScenarioEventsArgs parseScenarioEvents(final File scenarioEventsFile) {
		try {
			return xmlMapper.readValue(scenarioEventsFile, ScenarioEventsArgs.class);
		} catch (IOException e) {
			throw new InvalidScenarioException(format("Failed to parse scenario events file \"%s\"", eventFilePath), e);
		}
	}

	protected void runClientAgents() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		LongStream.rangeClosed(1, clientNumber).forEach(idx -> {
			final int randomPower = random.nextInt(minJobPower, maxJobPower);
			final int randomStart = random.nextInt(minStartTime, maxStartTime);
			final int randomEnd = random.nextInt(randomStart + 2, maxEndTime);
			final int randomDeadline = randomEnd + 3 + random.nextInt(maxDeadline);
			final String clientName = format("Client%d", timescaleDatabase.getNextClientId());

			final ClientAgentArgs clientAgentArgs = agentFactory.createClientAgent(clientName, String.valueOf(idx),
					randomPower, randomStart, randomEnd, randomDeadline, REAL_TIME);
			final AgentController agentController = factory.createAgentController(clientAgentArgs, scenario);
			factory.runAgentController(agentController, RUN_CLIENT_AGENT_DELAY);
		});
	}

	protected AgentController prepareManagingController(final ManagingAgentArgs managingAgentArgs) {
		try {
			var managingNode = new AgentNodeFactoryImpl().createAgentNode(managingAgentArgs, scenario);
			managingNode.setDatabaseClient(timescaleDatabase);
			guiController.addAgentNodeToGraph(managingNode);

			return mainContainer.createNewAgent(managingAgentArgs.getName(),
					"org.greencloud.managingsystem.agent.ManagingAgent",
					new Object[] { managingNode,
							guiController,
							managingAgentArgs.getSystemQualityThreshold(),
							scenario,
							mainContainer,
							managingAgentArgs.getPowerShortageThreshold(),
							managingAgentArgs.getDisabledActions()
					});
		} catch (StaleProxyException e) {
			throw new JadeControllerException("Failed to run managing agent controller", e);
		}
	}

	private ContainerController runMainContainer() throws ExecutionException, InterruptedException {
		final Profile profile = new ProfileImpl(localHostIp, Integer.parseInt(jadeIntraPort), platformId, true);
		profile.setParameter(Profile.ACCEPT_FOREIGN_AGENTS, "true");
		profile.setParameter(MTP_MULTI_MESSAGE, "false");

		if (localHostIp != null) {
			final String platformAddress = format("http://%s:%s/acc", localHostIp, jadeInterPort);
			profile.setParameter(Profile.MTPS, format("jade.mtp.http.MessageTransportProtocol(%s)", platformAddress));
			timescaleDatabase.writeAMSData("ams@" + platformId, platformAddress);

		}
		return executorService.submit(() -> jadeRuntime.createMainContainer(profile)).get();
	}

	private ContainerController runAgentsContainer() {
		final String platformHost = newPlatform && isNull(containerId) ? localHostIp : mainHostIp;
		final String containerName =
				newPlatform || isNull(containerId) ? defaultIfNull(locationId, "CNA") : containerId;

		var profile = new ProfileImpl(platformHost, Integer.parseInt(jadeIntraPort), platformId, false);
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.ACCEPT_FOREIGN_AGENTS, "true");
		profile.setParameter(MTP_MULTI_MESSAGE, "false");

		try {
			return executorService.submit(() -> jadeRuntime.createAgentContainer(profile)).get();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			if (containerName.equals(CLIENTS_CONTAINER_ID.toString())) {
				throw new JadeContainerException("Failed to create Agent Clients container", e);
			}
			throw new JadeContainerException("Failed to create CloudNetwork container", e);
		}
	}

	private void runJadeGui() throws StaleProxyException {
		final AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
		rma.start();

		if (runJadeSniffer) {
			final AgentController sniffer = mainContainer.createNewAgent("sniffeur", "jade.tools.sniffer.Sniffer",
					new Object[0]);
			sniffer.start();
		}
	}

	protected void updateSystemStartTime() {
		final Instant systemStart = timescaleDatabase.readSystemStartTime();
		setSystemStartTime(systemStart);
		guiController.reportSystemStartTime(systemStart);
	}

}
