package com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request;

import static com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.logs.WeatherCheckRequestLog.PERIODIC_CHECK_SENT_LOG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.behaviour.weathercheck.listener.ListenForWeatherData;
import com.greencloud.application.agents.greenenergy.domain.GreenEnergyAgentConstants;
import com.greencloud.application.messages.domain.constants.MessageProtocolConstants;

import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;

/**
 * Behaviour checks the current com.greencloud.application.weather periodically and evaluates if the power drop has happened
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
		super(agent, GreenEnergyAgentConstants.PERIODIC_WEATHER_CHECK_TIMEOUT);
		this.myGreenEnergyAgent = agent;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method initiates the com.greencloud.application.weather check
	 */
	@Override
	protected void onTick() {
		if (!myGreenEnergyAgent.getPowerJobs().isEmpty()) {
			logger.info(PERIODIC_CHECK_SENT_LOG, guid);
			final SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
			sequentialBehaviour.addSubBehaviour(
					new RequestWeatherData(myGreenEnergyAgent, MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL,
							MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL, null));
			sequentialBehaviour.addSubBehaviour(
					new ListenForWeatherData(myGreenEnergyAgent, null, MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL,
							MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL, sequentialBehaviour));
			myAgent.addBehaviour(sequentialBehaviour);
		}
	}
}
