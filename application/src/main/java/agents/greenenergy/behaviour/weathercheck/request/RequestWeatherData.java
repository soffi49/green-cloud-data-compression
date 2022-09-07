package agents.greenenergy.behaviour.weathercheck.request;

import static agents.greenenergy.behaviour.weathercheck.request.logs.WeatherCheckRequestLog.WEATHER_REQUEST_SENT_LOG;
import static java.util.Objects.isNull;
import static messages.domain.factory.PowerCheckMessageFactory.preparePowerCheckRequest;
import static utils.GUIUtils.displayMessageArrow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.greenenergy.GreenEnergyAgent;
import domain.ImmutableGreenSourceForecastData;
import domain.ImmutableGreenSourceWeatherData;
import domain.job.PowerJob;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour responsible for requesting weather data from monitoring agent
 */
public class RequestWeatherData extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(RequestWeatherData.class);

	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;
	private final String protocol;
	private final String conversationId;
	private final PowerJob powerJob;

	/**
	 * Behaviour constructor.
	 *
	 * @param greenEnergyAgent agent which is executing the behaviour
	 * @param protocol         protocol of the message
	 * @param conversationId   conversation id of the message
	 * @param powerJob         (optional) job for which the weather is to be checked
	 */
	public RequestWeatherData(GreenEnergyAgent greenEnergyAgent, String protocol, String conversationId,
			PowerJob powerJob) {
		this.myGreenEnergyAgent = greenEnergyAgent;
		this.protocol = protocol;
		this.conversationId = conversationId;
		this.powerJob = powerJob;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method which sends the request to the Monitoring Agent asking for the weather at the given location.
	 */
	@Override
	public void action() {
		logger.info(WEATHER_REQUEST_SENT_LOG, guid);
		final ACLMessage request = preparePowerCheckRequest(myGreenEnergyAgent, createMessageContent(), conversationId,
				protocol);
		displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getMonitoringAgent());
		myAgent.send(request);
	}

	private Object createMessageContent() {
		return isNull(powerJob) ?
				ImmutableGreenSourceWeatherData.builder()
						.location(myGreenEnergyAgent.getLocation())
						.build() :
				ImmutableGreenSourceForecastData.builder()
						.location(myGreenEnergyAgent.getLocation())
						.timetable(myGreenEnergyAgent.manage().getJobsTimetable(powerJob))
						.build();
	}
}
