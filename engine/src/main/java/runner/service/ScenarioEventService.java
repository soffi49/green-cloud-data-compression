package runner.service;

import static com.greencloud.commons.args.event.EventTypeEnum.NEW_CLIENT_EVENT;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static runner.configuration.ScenarioConfiguration.eventFilePath;
import static runner.constants.EngineConstants.POWER_SHORTAGE_EVENT_DELAY;

import java.io.File;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.commons.args.agent.client.ClientAgentArgs;
import com.greencloud.commons.args.event.EventArgs;
import com.greencloud.commons.args.event.newclient.NewClientEventArgs;
import com.greencloud.commons.args.event.powershortage.PowerShortageEventArgs;
import com.greencloud.commons.exception.InvalidScenarioEventStructure;
import com.greencloud.commons.scenario.ScenarioEventsArgs;
import com.greencloud.factory.AgentControllerFactory;
import com.gui.event.domain.PowerShortageEvent;

import jade.wrapper.AgentController;

/**
 * Service containing method used to handle events defined for the given scenario
 */
public class ScenarioEventService {

	private static final Logger logger = LoggerFactory.getLogger(ScenarioEventService.class);

	private final AbstractScenarioService scenarioService;

	/**
	 * Default constructor
	 *
	 * @param abstractScenarioService scenario service
	 */
	public ScenarioEventService(final AbstractScenarioService abstractScenarioService) {
		this.scenarioService = abstractScenarioService;
	}

	/**
	 * Method is responsible for reading and scheduling scenario events
	 */
	public void runScenarioEvents() {
		if (eventFilePath.isPresent()) {
			final File scenarioEventsFile = scenarioService.readFile(eventFilePath.get());
			final ScenarioEventsArgs scenarioEvents = scenarioService.parseScenarioEvents(scenarioEventsFile);
			validateScenarioStructure(scenarioEvents);
			scheduleScenarioEvents(scenarioEvents.getEventArgs());
		}
	}

	private void validateScenarioStructure(final ScenarioEventsArgs scenarioEvents) {
		final List<NewClientEventArgs> newClientEvents = scenarioEvents.getEventArgs().stream()
				.filter(eventArgs -> eventArgs.getType().equals(NEW_CLIENT_EVENT))
				.map(NewClientEventArgs.class::cast)
				.toList();
		validateClientDuplicates(newClientEvents);
	}

	private void validateClientDuplicates(final List<NewClientEventArgs> clientEventArgs) {
		final Set<String> clientNameSet = new HashSet<>();
		final Set<Integer> jobIdSet = new HashSet<>();

		clientEventArgs.forEach(client -> {
			if (!clientNameSet.add(client.getName())) {
				throw new InvalidScenarioEventStructure(
						String.format("Clients must have unique names. Duplicated client name: %s", client.getName()));
			}
			if (!jobIdSet.add(client.getJobId())) {
				throw new InvalidScenarioEventStructure(
						String.format("Specified job ids must be unique. Duplicated job id: %d", client.getJobId()));
			}
		});
	}

	private void scheduleScenarioEvents(final List<EventArgs> eventArgs) {
		logger.info("Scheduling scenario events...");
		final ScheduledExecutorService executor = newSingleThreadScheduledExecutor();
		eventArgs.forEach(event -> executor.schedule(() -> runEvent(event), event.getOccurrenceTime(), SECONDS));
		scenarioService.factory.shutdownAndAwaitTermination(executor);
	}

	private void runEvent(final EventArgs event) {
		switch (event.getType()) {
			case NEW_CLIENT_EVENT -> runNewClientEvent(event);
			case POWER_SHORTAGE_EVENT -> triggerPowerShortage(event);
		}
	}

	private void runNewClientEvent(final EventArgs event) {
		final AgentControllerFactory factory = scenarioService.factory;
		final NewClientEventArgs newClientEvent = (NewClientEventArgs) event;
		final ClientAgentArgs clientAgentArgs = scenarioService.agentFactory.createClientAgent(newClientEvent);

		final AgentController agentController = factory.createAgentController(clientAgentArgs, null);
		factory.runAgentController(agentController, 0);
	}

	private void triggerPowerShortage(final EventArgs event) {
		final PowerShortageEventArgs powerShortageArgs = (PowerShortageEventArgs) event;
		final Instant eventOccurrence = Instant.now().plusSeconds(POWER_SHORTAGE_EVENT_DELAY);
		final PowerShortageEvent eventData = new PowerShortageEvent(eventOccurrence,
				powerShortageArgs.getNewMaximumCapacity(), powerShortageArgs.isFinished(),
				powerShortageArgs.getCause());
		scenarioService.guiController.triggerPowerShortageEvent(eventData, powerShortageArgs.getAgentName());
	}
}
