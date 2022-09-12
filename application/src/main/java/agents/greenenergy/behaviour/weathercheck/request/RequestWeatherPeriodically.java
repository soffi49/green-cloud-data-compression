package agents.greenenergy.behaviour.weathercheck.request;

import static agents.greenenergy.behaviour.weathercheck.request.logs.WeatherCheckRequestLog.PERIODIC_CHECK_SENT_LOG;
import static agents.greenenergy.domain.GreenEnergyAgentConstants.PERIODIC_WEATHER_CHECK_TIMEOUT;
import static messages.domain.constants.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.weathercheck.listener.ListenForWeatherData;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;

/**
 * Behaviour checks the current weather periodically and evaluates if the power drop has happened
 */
public class RequestWeatherPeriodically extends TickerBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(RequestWeatherPeriodically.class);

	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;

	/**
	 * Behaviour constructor
	 *
	 * @param agent agent executing the behaviour
	 */
	public RequestWeatherPeriodically(GreenEnergyAgent agent) {
		super(agent, PERIODIC_WEATHER_CHECK_TIMEOUT);
		this.myGreenEnergyAgent = agent;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method initiates the weather check
	 */
	@Override
	protected void onTick() {
		if (!myGreenEnergyAgent.getPowerJobs().isEmpty()) {
			logger.info(PERIODIC_CHECK_SENT_LOG, guid);
			final SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
			sequentialBehaviour.addSubBehaviour(
					new RequestWeatherData(myGreenEnergyAgent, PERIODIC_WEATHER_CHECK_PROTOCOL,
							PERIODIC_WEATHER_CHECK_PROTOCOL, null));
			sequentialBehaviour.addSubBehaviour(
					new ListenForWeatherData(myGreenEnergyAgent, null, PERIODIC_WEATHER_CHECK_PROTOCOL,
							PERIODIC_WEATHER_CHECK_PROTOCOL, sequentialBehaviour));
			myAgent.addBehaviour(sequentialBehaviour);
		}
	}
}
