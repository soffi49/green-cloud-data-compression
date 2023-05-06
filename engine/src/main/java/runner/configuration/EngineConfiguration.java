package runner.configuration;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.io.File.separator;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Properties;

import com.greencloud.commons.exception.InvalidPropertiesException;
import runner.service.AbstractScenarioService;

/**
 * Constants used to set up the system (mostly agent platforms)
 * The configuration is by default injected from .properties files
 */
public final class EngineConfiguration {

	private static final String PROPERTIES_DIR = "properties";
	private static  String SYSTEM_PROPERTIES_FILE = "system.properties";

	/**
	 * Port used for the intra-platform (i.e. inside the platform) agent communication
	 */
	public static String jadeIntraPort;
	/**
	 * Port used for the inter-platform (i.e. between the platforms) agent communication
	 */
	public static String jadeInterPort;
	/**
	 * Boolean value denoting if the container is the main one
	 * (i.e. is the main container of the entire system containing SchedulerAgent and ManagingAgent)
	 */
	public static boolean mainHost;
	/**
	 * Boolean value denoting if the system should start the container in a new JADE platform.
	 */
	public static boolean newPlatform;
	/**
	 * Identifier value denoting which CNA is to be started at the given container.
	 * If the value is null then it starts all CNAs without specified locationId at the same container.
	 * If it is set to "Clients<number>" then it denotes that the container which generates clients is to be started
	 */
	public static String locationId;
	/**
	 * Identifier value denoting which Servers are to be run at the given container
	 * If the identifier is null then it means that it will run the container with CNA and Servers which have no
	 * containerId specified.
	 * The value is treated as null if the newPlatform value is set to true.
	 */
	public static String containerId;
	/**
	 * Name of the platform under which the containers are to be created
	 */
	public static String platformId;
	/**
	 * Address of the host that runs the main container of the entire system
	 */
	public static String mainHostIp;
	/**
	 * Identifier of the agent platform in which the main container of the entire system runs
	 * This parameter is used to find the relevant DF
	 */
	public static String mainHostPlatformId;
	/**
	 * Port used for the inter-platform (i.e. between the platforms) agent communication of the agent platform
	 * that runs main container of the entire system
	 * This parameter is used to find the relevant DF
	 */
	public static String mainHostInterPort;
	/**
	 * Address of the main Directory Facilitator
	 */
	public static String mainDFAddress;
	/**
	 * Host's local IP that is used by the JADE Framework within Docker container for networking with other containers
	 * on different hosts. Must be provided as Docker containers by default don't have access to the network outside the
	 * Docker container.
	 */
	public static String localHostIp;
	/**
	 * Local IP of the host running Timescale database.
	 */
	public static String databaseHostIp;
	/**
	 * Local IP of the host running socket server.
	 */
	public static String websocketHostIp;
	/**
	 * Address of web sockets
	 */
	public static String websocketAddress;
	/**
	 * Flag indicates if the JADE GUI should be started along with the main container
	 */
	public static boolean runJadeGUI;
	/**
	 * Flag indicates if the JADE Sniffer should be started along with the main container
	 */
	public static boolean runJadeSniffer;

	/**
	 * Method sets the system properties file
	 *
	 * @param systemPropertiesFile name of the system properties file
	 */
	public static void setSystemPropertiesFile(final String systemPropertiesFile) {
		SYSTEM_PROPERTIES_FILE = systemPropertiesFile;
	}

	/**
	 * Method reads the properties set for the system set up at the given .properties file
	 */
	public static void readSystemProperties() {
		final String propertiesFile = separator + PROPERTIES_DIR + separator + SYSTEM_PROPERTIES_FILE;
		final Properties props = new Properties();

		try {
			props.load(AbstractScenarioService.class.getClassLoader().getResourceAsStream(propertiesFile));

			setUpCommunication(props);
			setUpContainer(props);
			setUpMainHost(props);
			setUpExternalConnection(props);
			setUpJADEGUISettings(props);

		} catch (final IOException e) {
			throw new InvalidPropertiesException("Could not read properties file:", e);
		}
	}

	private static void setUpCommunication(final Properties props) {
		jadeInterPort = ifNotBlankThenGetOrElse(props.getProperty("mtp.inter"), "7778");
		jadeIntraPort = ifNotBlankThenGetOrElse(props.getProperty("mtp.intra"), "1099");
	}

	private static void setUpContainer(final Properties props) {
		mainHost = parseBoolean(ifNotBlankThenGetOrElse(props.getProperty("container.mainhost"), "true"));
		newPlatform = parseBoolean(ifNotBlankThenGetOrElse(props.getProperty("container.createnew"), "false"));
		locationId = ifNotBlankThenGetOrElse(props.getProperty("container.locationId"), null);
		containerId = newPlatform ? null : ifNotBlankThenGetOrElse(props.getProperty("container.containerId"), null);
		platformId = ifNotBlankThenGetOrElse(props.getProperty("container.platformid"), "MainPlatform");

		if (!isNullOrEmpty(props.getProperty("container.localhostip"))) {
			localHostIp = props.getProperty("container.localhostip");
		}
	}

	private static void setUpMainHost(final Properties props) {
		mainHostIp = ifNotBlankThenGetOrElse(props.getProperty("main.hostip"), "127.0.0.1");
		mainHostPlatformId = ifNotBlankThenGetOrElse(props.getProperty("main.platformid"), "MainPlatform");
		mainHostInterPort = ifNotBlankThenGetOrElse(props.getProperty("main.inter"), "7778");
		mainDFAddress = format("http://%s:%s/acc", mainHostIp, mainHostInterPort);
	}

	private static void setUpExternalConnection(final Properties props) {
		databaseHostIp = ifNotBlankThenGetOrElse(props.getProperty("service.database.hostip"), "localhost");
		websocketHostIp = ifNotBlankThenGetOrElse(props.getProperty("service.websocket.hostip"), "localhost");
		websocketAddress = format("ws://%s:8080/", websocketHostIp);
	}

	private static void setUpJADEGUISettings(final Properties props) {
		runJadeGUI = parseBoolean(ifNotBlankThenGetOrElse(props.getProperty("jade.rungui"), "true"));
		runJadeSniffer = parseBoolean(ifNotBlankThenGetOrElse(props.getProperty("jade.runsniffer"), "false"));
	}

	private static String ifNotBlankThenGetOrElse(final String property, final String defaultVal) {
		return isNullOrEmpty(property) ? defaultVal : property;
	}
}
