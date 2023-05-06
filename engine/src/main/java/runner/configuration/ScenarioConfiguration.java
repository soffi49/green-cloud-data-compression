package runner.configuration;

import static java.io.File.separator;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static runner.constants.EngineConstants.PROPERTIES_DIR;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import com.greencloud.commons.exception.InvalidPropertiesException;
import runner.service.AbstractScenarioService;

/**
 * Constants used to set up the scenario run in the system
 * The configuration is by default injected from .properties files
 */
public class ScenarioConfiguration {

	private static final String SCENARIOS_DIR = "scenarios";
	private static final String SCENARIO_PROPERTIES_FILE = "scenario.properties";

	/**
	 * Path to the file containing scenario structure
	 */
	public static String scenarioFilePath;

	/**
	 * Optional path to the file containing scenario events
	 */
	public static Optional<String> eventFilePath;

	/**
	 * Number of clients run by default at the given scenario
	 */
	public static long clientNumber;

	/**
	 * Maximum power required for client job execution
	 */
	public static int maxJobPower;

	/**
	 * Minimum power required for client job execution
	 */
	public static int minJobPower;

	/**
	 * Minimum time after which the client job is to be started
	 * (in real-time seconds)
	 */
	public static int minStartTime;

	/**
	 * Maximum time after which the client job is to be started
	 * (in real-time seconds)
	 */
	public static int maxStartTime;

	/**
	 * Maximum time after which the client job is to be finished
	 * (in real-time seconds)
	 */
	public static int maxEndTime;

	/**
	 * Maximum client's job execution deadline time
	 * (in real-time seconds)
	 */
	public static int maxDeadline;

	/**
	 * Method reads the properties set for the given scenario execution
	 */
	public static void readScenarioProperties() {
		final String propertiesFile = separator + PROPERTIES_DIR + separator + SCENARIO_PROPERTIES_FILE;
		final Properties props = new Properties();

		try {
			props.load(AbstractScenarioService.class.getClassLoader().getResourceAsStream(propertiesFile));

			final boolean useSubDirectory = parseBoolean(props.getProperty("scenario.usesubdirectory"));
			final boolean useEvents = parseBoolean(props.getProperty("scenario.runEvents"));

			final String scenarioPath = useSubDirectory ? retrieveScenarioSubDirectory(props) : SCENARIOS_DIR;

			scenarioFilePath = scenarioPath + separator + props.getProperty("scenario.structure");
			eventFilePath = useEvents
					? Optional.of(scenarioPath + separator + props.getProperty("scenario.events"))
					: Optional.empty();
			clientNumber = parseInt(props.getProperty("scenario.clients.number"));
			minJobPower = parseInt(props.getProperty("scenario.clients.minpower"));
			maxJobPower = parseInt(props.getProperty("scenario.clients.maxpower"));
			minStartTime = parseInt(props.getProperty("scenario.clients.minstarttime"));
			maxStartTime = parseInt(props.getProperty("scenario.clients.maxstarttime"));
			maxEndTime = parseInt(props.getProperty("scenario.clients.maxendtime"));
			maxDeadline = parseInt(props.getProperty("scenario.clients.maxdeadline"));

		} catch (final IOException e) {
			throw new InvalidPropertiesException("Could not read properties file:", e);
		}
	}

	/**
	 * Method retrieves the directory containing the scenario used in the system run
	 */
	protected static String retrieveScenarioSubDirectory(final Properties props) {
		final String subDirectory = props.getProperty("scenario.subdirectory").replace(".", separator);
		return SCENARIOS_DIR + separator + subDirectory;
	}
}
