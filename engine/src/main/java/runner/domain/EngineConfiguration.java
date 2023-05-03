package runner.domain;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.io.File.separator;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.util.Properties;

import runner.exception.InvalidPropertiesException;
import runner.service.AbstractScenarioService;

/**
 * Constants used to set up the system (mostly agent platforms)
 * The configuration is by default injected from .properties files
 */
public final class EngineConfiguration {

	private static final String PROPERTIES_DIR = "properties";
	private static String SYSTEM_PROPERTIES_FILE = "system2.properties";

	/**
	 * Port used for the intra-platform (i.e. inside the platform) agent communication
	 */
	public static String jadeIntraPort;

	/**
	 * Port used for the inter-platform (i.e. between the platforms) agent communication
	 */
	public static String jadeInterPort;

	/**
	 * Port of "parent" agent platform host used for the inter-platform (i.e. between the platforms) agent communication
	 * This parameter is used to find the relevant DF
	 */
	public static String jadeInterParentPort;

	/**
	 * Boolean value denoting if the agent platform is the main one
	 * (i.e. is the main platform of the entire system containing SchedulerAgent and ManagingAgent)
	 */
	public static boolean mainHost;

	/**
	 * Boolean value denoting if the system should start the container in a new JADE platform.
	 */
	public static boolean newPlatform;

	/**
	 * Identifier value denoting which CNA from scenario will run on given host
	 * (e.g. for hostId = 1 host will run all agents defined under CNA1)
	 */
	public static int hostId;

	/**
	 * IP of the main container host to which other hosted containers should connect
	 * (default value 127.0.0.1 is used only by the main host)
	 */
	public static String mainHostIp;

	/**
	 * Identifier of the "parent" agent platform that contains
	 * (i.e. platform which contains the agent that is higher in the hierarchy,
	 * e.g. for CNA's it would be the identifier of the platform containing the SchedulerAgent)
	 * This parameter is used to find the relevant DF
	 */
	public static String mainHostPlatformId;

	/**
	 * Host's local IP that is used by the JADE Framework within Docker container for networking with other containers
	 * on different hosts. Must be provided as Docker containers by default don't have access to the network outside the
	 * Docker container.
	 */
	public static String localHostIp;

	/**
	 * Flag indicating if the platforms should communicate using XMPP MTP
	 * (e.g. to test the system under firewall restrictions)
	 */
	public static boolean useXMPP;

	/**
	 * Domain name of XMPP server
	 */
	public static String xmppServer;

	/**
	 * Name of the XMPP user
	 */
	public static String xmppUser;

	/**
	 * Password of the XMPP user
	 */
	public static String xmppPassword;

	/**
	 * Local IP of the host running Timescale database.
	 */
	public static String databaseHostIp;

	/**
	 * Local IP of the host running socket server.
	 */
	public static String websocketHostIp;

	/**
	 * Flag indicates if the JADE GUI should be started along with the main container
	 */
	public static boolean runJadeGUI;


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

			jadeInterPort = props.getProperty("mtp.inter");
			jadeIntraPort = props.getProperty("mtp.intra");
			jadeInterParentPort = props.getProperty("mtp.interparent");
			mainHost = parseBoolean(props.getProperty("platform.mainhost"));
			newPlatform = parseBoolean(props.getProperty("platform.createnew"));
			hostId = parseInt(props.getProperty("platform.hostid"));
			mainHostIp = props.getProperty("main.hostip");
			mainHostPlatformId = props.getProperty("main.platformid");
			useXMPP = parseBoolean(props.getProperty("xmpp.isenabled"));
			xmppServer = props.getProperty("xmpp.server");
			xmppUser = props.getProperty("xmpp.user");
			xmppPassword = props.getProperty("xmpp.password");
			databaseHostIp = props.getProperty("service.database.hostip");
			websocketHostIp = props.getProperty("service.websocket.hostip");
			runJadeGUI = parseBoolean(props.getProperty("jade.rungui"));

			if (!isNullOrEmpty(props.getProperty("localhostip"))) {
				localHostIp = props.getProperty("localhostip");
			}

		} catch (final IOException e) {
			throw new InvalidPropertiesException("Could not read properties file:", e);
		}
	}
}
