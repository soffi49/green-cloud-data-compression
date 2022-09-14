package com.greencloud.application.agents.monitoring.behaviour;

import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;
import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.monitoring.MonitoringAgent;
import com.greencloud.application.agents.monitoring.behaviour.logs.WeatherServingLog;
import com.greencloud.application.agents.monitoring.behaviour.templates.WeatherServingMessageTemplates;
import com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants;
import com.greencloud.application.domain.GreenSourceForecastData;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.mapper.JsonMapper;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the upcoming forecast requests
 */
public class ServeForecastWeather extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ServeForecastWeather.class);
	private static final Random STUB_DATA_RANDOM = new Random();
	private final MonitoringAgent monitoringAgent;
	private final String guid;

	/**
	 * Behaviour constructor.
	 *
	 * @param monitoringAgent agent which is executing the behaviour
	 */
	public ServeForecastWeather(MonitoringAgent monitoringAgent) {
		this.monitoringAgent = monitoringAgent;
		this.guid = monitoringAgent.getName();
	}

	/**
	 * Method listens for the request for com.greencloud.application.weather data coming from the Green Source Agents.
	 * It retrieves the forecast information for the given location and forwards it as a reply to the sender.
	 */
	@Override
	public void action() {
		final ACLMessage message = monitoringAgent.receive(WeatherServingMessageTemplates.SERVE_FORECAST_TEMPLATE);

		if (Objects.nonNull(message)) {
			logger.info(WeatherServingLog.SERVE_FORECAST_LOG, guid);
			final GreenSourceForecastData requestData = readMessageContent(message, GreenSourceForecastData.class);
			final ACLMessage response = prepareWeatherResponse(message, requestData);

			displayMessageArrow(monitoringAgent, message.getSender());
			monitoringAgent.send(response);
		} else {
			block();
		}
	}

	private ACLMessage prepareWeatherResponse(final ACLMessage message, final GreenSourceForecastData requestData) {
		final boolean isPeriodicCheck = message.getConversationId().equals(PERIODIC_WEATHER_CHECK_PROTOCOL);
		final ACLMessage response = message.createReply();
		response.setPerformative(INFORM);
		try {
			if (isPeriodicCheck && (double) STUB_DATA_RANDOM.nextInt(100) / 100 < MonitoringAgentConstants.BAD_STUB_PROBABILITY) {
				response.setContent(JsonMapper.getMapper().writeValueAsString(MonitoringAgentConstants.BAD_STUB_DATA));
			} else if (MonitoringAgentConstants.OFFLINE_MODE) {
				response.setContent(JsonMapper.getMapper().writeValueAsString(MonitoringAgentConstants.STUB_DATA));
			} else {
				response.setContent(JsonMapper.getMapper().writeValueAsString(useApi(requestData)));
			}
		} catch (IOException e) {
			e.printStackTrace();
			response.setPerformative(REFUSE);
		}
		response.setConversationId(message.getConversationId());
		return response;
	}

	private MonitoringData useApi(GreenSourceForecastData requestData) {
		return monitoringAgent.manageWeather().getForecast(requestData);
	}
}
