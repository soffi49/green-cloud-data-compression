package com.greencloud.application.agents.monitoring.behaviour;

import static com.greencloud.application.agents.monitoring.behaviour.logs.WeatherServingLog.SERVE_FORECAST_LOG;
import static com.greencloud.application.agents.monitoring.behaviour.templates.WeatherServingMessageTemplates.SERVE_FORECAST_TEMPLATE;
import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.BAD_STUB_DATA;
import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.MAX_NUMBER_OF_WEATHER_REQUESTS;
import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.STUB_DATA;
import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.WEATHER_REQUESTS_IN_BATCH;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;
import static com.greencloud.application.messages.factory.PowerCheckMessageFactory.prepareWeatherDataResponse;
import static com.greencloud.application.utils.MessagingUtils.readMessageContent;
import static com.greencloud.commons.constants.LoggingConstant.MDC_AGENT_NAME;
import static java.lang.Math.max;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.monitoring.MonitoringAgent;
import com.greencloud.application.domain.agent.GreenSourceForecastData;
import com.greencloud.application.domain.agent.GreenSourceWeatherData;
import com.greencloud.application.domain.weather.MonitoringData;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the upcoming forecast requests
 */
public class ServeForecastWeather extends CyclicBehaviour implements Serializable {

	private static final Logger logger = getLogger(ServeForecastWeather.class);
	private static final Random STUB_DATA_RANDOM = new Random();
	private final MonitoringAgent monitoringAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param monitoringAgent agent which is executing the behaviour
	 */
	public ServeForecastWeather(MonitoringAgent monitoringAgent) {
		this.monitoringAgent = monitoringAgent;
	}

	/**
	 * Method listens for the request for weather data coming from the Green Source Agents.
	 * It retrieves the forecast information for the given location and forwards it as a reply to the sender.
	 */
	@Override
	public void action() {
		final List<ACLMessage> messages = monitoringAgent.receive(SERVE_FORECAST_TEMPLATE,
				MAX_NUMBER_OF_WEATHER_REQUESTS);

		if (nonNull(messages)) {
			ListUtils.partition(messages, WEATHER_REQUESTS_IN_BATCH).stream().parallel()
					.forEach(list -> list.forEach(msg -> {
						MDC.put(MDC_AGENT_NAME, myAgent.getLocalName());
						logger.info(SERVE_FORECAST_LOG);
						final boolean isPeriodicCheck = msg.getConversationId().equals(PERIODIC_WEATHER_CHECK_PROTOCOL);
						final MonitoringData data = isPeriodicCheck ?
								getWeatherDataForPeriodicCheck(msg) :
								getWeatherForecast(msg);

						monitoringAgent.send(prepareWeatherDataResponse(data, msg));
					}));
		} else {
			block();
		}
	}

	private MonitoringData getWeatherForecast(final ACLMessage message) {
		final GreenSourceForecastData requestData = readMessageContent(message, GreenSourceForecastData.class);
		return monitoringAgent.isOfflineMode() ? STUB_DATA : monitoringAgent.manageWeather().getForecast(requestData);
	}

	private MonitoringData getWeatherDataForPeriodicCheck(final ACLMessage message) {
		final GreenSourceWeatherData requestData = readMessageContent(message, GreenSourceWeatherData.class);
		final double badWeatherPredictionProbability =
				max(monitoringAgent.getBadStubProbability() - requestData.getPredictionError(), 0);

		if ((double) STUB_DATA_RANDOM.nextInt(100) / 100 < badWeatherPredictionProbability) {
			return BAD_STUB_DATA;
		} else {
			return monitoringAgent.isOfflineMode() ?
					STUB_DATA :
					monitoringAgent.manageWeather().getWeather(requestData);
		}
	}

}
