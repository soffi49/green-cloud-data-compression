package runner.service;

import static com.greencloud.application.utils.TimeUtils.setSystemStartTime;
import static com.greencloud.commons.args.agent.client.ClientTimeType.REAL_TIME;
import static jade.core.Runtime.instance;
import static jade.wrapper.AgentController.ASYNC;
import static java.lang.String.format;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import static runner.constants.EngineConstants.CONTAINER_NAME_PREFIX;
import static runner.constants.EngineConstants.GRAPH_INITIALIZATION_DELAY;
import static runner.constants.EngineConstants.RUN_AGENT_DELAY;
import static runner.constants.EngineConstants.RUN_CLIENT_AGENT_DELAY;
import static runner.domain.EngineConfiguration.databaseHostIp;
import static runner.domain.EngineConfiguration.hostId;
import static runner.domain.EngineConfiguration.jadeInterPort;
import static runner.domain.EngineConfiguration.jadeIntraPort;
import static runner.domain.EngineConfiguration.localHostIp;
import static runner.domain.EngineConfiguration.newPlatform;
import static runner.domain.EngineConfiguration.runJadeGUI;
import static runner.domain.EngineConfiguration.useXMPP;
import static runner.domain.EngineConfiguration.websocketHostIp;
import static runner.domain.EngineConfiguration.xmppPassword;
import static runner.domain.EngineConfiguration.xmppServer;
import static runner.domain.EngineConfiguration.xmppUser;
import static runner.domain.ScenarioConfiguration.clientNumber;
import static runner.domain.ScenarioConfiguration.eventFilePath;
import static runner.domain.ScenarioConfiguration.maxDeadline;
import static runner.domain.ScenarioConfiguration.maxEndTime;
import static runner.domain.ScenarioConfiguration.maxJobPower;
import static runner.domain.ScenarioConfiguration.maxStartTime;
import static runner.domain.ScenarioConfiguration.minJobPower;
import static runner.domain.ScenarioConfiguration.minStartTime;
import static runner.domain.ScenarioConfiguration.scenarioFilePath;
import static runner.domain.enums.ContainerTypeEnum.CLIENTS_CONTAINER_ID;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.database.knowledge.timescale.TimescaleDatabase;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.args.agent.client.ClientAgentArgs;
import com.greencloud.commons.args.agent.client.ImmutableClientAgentArgs;
import com.greencloud.commons.scenario.ScenarioEventsArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;
import com.gui.agents.AbstractAgentNode;
import com.gui.controller.GuiController;
import com.gui.controller.GuiControllerImpl;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.exception.InvalidScenarioException;
import runner.exception.JadeContainerException;
import runner.exception.JadeControllerException;
import runner.factory.AgentControllerFactory;

/**
 * Abstract class serving as common base to Single and Multi Scenario Services.
 * It handles creation of Main and Agent's Containers as well as Agent's Controllers.
 * It is also responsible for running Agent's and Agent's clients.
 */
public abstract class AbstractScenarioService {

	protected static final XmlMapper xmlMapper = new XmlMapper();
	protected static final ExecutorService executorService = Executors.newCachedThreadPool();
	private static final Logger logger = LoggerFactory.getLogger(AbstractScenarioService.class);

	protected final ScenarioEventService eventService;
	protected final GuiController guiController;
	protected final TimescaleDatabase timescaleDatabase;
	protected final Runtime jadeRuntime;

	protected final ContainerController mainContainer;
	protected final ContainerController agentContainer;

	protected ScenarioStructureArgs scenario;

	/**
	 * Constructor called by {@link MultiContainerScenarioService} and {@link SingleContainerScenarioService}
	 * Launches gui and the main controller.
	 * In case of MultiContainer case runs environment only for the main host.
	 */
	protected AbstractScenarioService()
			throws ExecutionException, InterruptedException, StaleProxyException {
		this.guiController = new GuiControllerImpl(format("ws://%s:8080/", websocketHostIp));
		this.eventService = new ScenarioEventService(this);

		this.jadeRuntime = instance();
		this.timescaleDatabase = new TimescaleDatabase(databaseHostIp);

		timescaleDatabase.initDatabase();
		executorService.execute(guiController);
		mainContainer = runMainController();
		agentContainer = null;

		if (runJadeGUI) {
			runJadeGui();
		}
	}

	/**
	 * Runs remote AgentContainer with GUI.
	 *
	 * @param hostId     number of the host id
	 * @param mainHostIp IP address of the main host
	 */
	protected AbstractScenarioService(final Integer hostId, final String mainHostIp)
			throws ExecutionException, InterruptedException, StaleProxyException {
		this.guiController = new GuiControllerImpl(format("ws://%s:8080/", websocketHostIp));
		this.eventService = new ScenarioEventService(this);

		this.jadeRuntime = instance();
		this.timescaleDatabase = new TimescaleDatabase(databaseHostIp);

		executorService.execute(guiController);
		mainContainer = newPlatform ? runMainController() : null;
		agentContainer = runAgentsContainer(CONTAINER_NAME_PREFIX + hostId.toString(), mainHostIp);

		if (newPlatform && runJadeGUI) {
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

	protected AgentController runAgentController(final AgentArgs args, final ScenarioStructureArgs scenario,
			final AgentControllerFactory factory) {
		try {
			final AgentController agentController = factory.createAgentController(args, scenario);
			final AbstractAgentNode agentNode = factory.createAgentNode(args, scenario);
			agentNode.setDatabaseClient(timescaleDatabase);
			guiController.addAgentNodeToGraph(agentNode);
			agentController.putO2AObject(guiController, ASYNC);
			agentController.putO2AObject(agentNode, ASYNC);

			logger.info("Created {} agent.", args.getName());
			return agentController;
		} catch (StaleProxyException e) {
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	protected void runClientAgents(final AgentControllerFactory factory) {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		LongStream.rangeClosed(1, clientNumber).forEach(idx -> {
			final int randomPower = random.nextInt(minJobPower, maxJobPower);
			final int randomStart = random.nextInt(minStartTime, maxStartTime);
			final int randomEnd = random.nextInt(randomStart + 2, maxEndTime);
			final int randomDeadline = randomEnd + 3 + random.nextInt(maxDeadline);
			final int clientId = timescaleDatabase.getNextClientId();

			final ClientAgentArgs clientAgentArgs = ImmutableClientAgentArgs.builder()
					.name(format("Client%d", clientId))
					.jobId(String.valueOf(idx))
					.power(String.valueOf(randomPower))
					.start(String.valueOf(randomStart))
					.end(String.valueOf(randomEnd))
					.deadline(String.valueOf(randomDeadline))
					.timeType(REAL_TIME)
					.build();

			final AgentController agentController = runAgentController(clientAgentArgs, null, factory);
			runAgent(agentController, RUN_CLIENT_AGENT_DELAY);
		});
	}

	protected void runAgents(final List<AgentController> controllers) {
		var scheduledExecutor = newSingleThreadScheduledExecutor();
		scheduledExecutor.schedule(() -> controllers.forEach(controller -> runAgent(controller, RUN_AGENT_DELAY)),
				GRAPH_INITIALIZATION_DELAY, SECONDS);
		shutdownAndAwaitTermination(scheduledExecutor);
	}

	protected void runAgent(final AgentController controller, long pause) {
		try {
			controller.start();
			controller.activate();
			TimeUnit.MILLISECONDS.sleep(pause);
		} catch (StaleProxyException | InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	protected void shutdownAndAwaitTermination(ExecutorService executorService) {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException ie) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private ContainerController runMainController() throws ExecutionException, InterruptedException {
		final String platformId = !newPlatform ? "MainPlatform" : format("Platform%d", hostId);
		final String containerName = !newPlatform ? "Main-Container" : format("Platform%d-Main-Container", hostId);

		final Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, localHostIp);
		profile.setParameter(Profile.MAIN_PORT, jadeIntraPort);
		profile.setParameter(Profile.PLATFORM_ID, platformId);
		profile.setParameter(Profile.ACCEPT_FOREIGN_AGENTS, "true");

		if (localHostIp != null) {
			final String platformAddress = format("http://%s:%s/acc", localHostIp, jadeInterPort);

			profile.setParameter(Profile.EXPORT_HOST, localHostIp);
			if (useXMPP) {
				profile.setParameter(Profile.MTPS, "jade.mtp.xmpp.MessageTransportProtocol");
				profile.setParameter("jade_mtp_xmpp_server", xmppServer);
				profile.setParameter("jade_mtp_xmpp_username", xmppUser);
				profile.setParameter("jade_mtp_xmpp_password", xmppPassword);
			} else {
				profile.setParameter(Profile.MTPS,
						format("jade.mtp.http.MessageTransportProtocol(%s)", platformAddress));
			}
			timescaleDatabase.writeAMSData("ams@" + platformId, platformAddress);
		}
		return executorService.submit(() -> jadeRuntime.createMainContainer(profile)).get();
	}

	protected ContainerController runAgentsContainer(String containerName, String host) {
		var profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, jadeIntraPort);
		profile.setParameter(Profile.ACCEPT_FOREIGN_AGENTS, "true");

		if (localHostIp != null) {
			profile.setParameter(Profile.EXPORT_HOST, localHostIp);
		}
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

	protected void updateSystemStartTime() {
		final Instant systemStart = timescaleDatabase.readSystemStartTime();
		setSystemStartTime(systemStart);
		guiController.reportSystemStartTime(systemStart);
	}

	private void runJadeGui() throws StaleProxyException {
		final AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
		rma.start();
	}
}
