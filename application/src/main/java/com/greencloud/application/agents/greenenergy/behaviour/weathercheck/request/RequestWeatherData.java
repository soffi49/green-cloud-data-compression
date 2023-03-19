package com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request;

import static com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.logs.WeatherCheckRequestLog.WEATHER_REQUEST_SENT_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.factory.PowerCheckMessageFactory.preparePowerCheckRequest;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.weather.MonitoringData;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.domain.job.ServerJob;

import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Behaviour responsible for requesting and processing weather data from Monitoring Agent.
 */
public class RequestWeatherData extends AchieveREInitiator {

	private static final Logger logger = getLogger(RequestWeatherData.class);

	private final BiConsumer<MonitoringData, Exception> weatherHandler;
	private final Runnable refusalHandler;

	private RequestWeatherData(final GreenEnergyAgent greenEnergyAgent, final ACLMessage request,
			final BiConsumer<MonitoringData, Exception> weatherHandler,
			final Runnable refusalHandler) {
		super(greenEnergyAgent, request);

		this.weatherHandler = weatherHandler;
		this.refusalHandler = refusalHandler;
	}

	/**
	 * Method creates the RequestWeatherData behaviour.
	 *
	 * @param greenEnergyAgent agent which is executing the behaviour
	 * @param protocol         protocol of the message
	 * @param conversationId   conversation id of the message
	 * @param weatherHandler   function executed when weather data is received from Monitoring Agent (function accepts
	 *                         Monitoring data and optional error that may occur if incorrect message format was received)
	 * @param refusalHandler   function executed when Monitoring agent refuses to retrieve weather data
	 * @param serverJob        (optional) job for which the weather is to be checked
	 */
	public static RequestWeatherData createWeatherRequest(final GreenEnergyAgent greenEnergyAgent,
			final String protocol, final String conversationId,
			final BiConsumer<MonitoringData, Exception> weatherHandler,
			final Runnable refusalHandler, final ServerJob serverJob) {
		final ACLMessage request = preparePowerCheckRequest(greenEnergyAgent, serverJob, conversationId, protocol);

		if (nonNull(serverJob)) {
			MDC.put(MDC_JOB_ID, serverJob.getJobId());
		}

		logger.info(WEATHER_REQUEST_SENT_LOG);
		return new RequestWeatherData(greenEnergyAgent, request, weatherHandler, refusalHandler);
	}

	/**
	 * Method creates the RequestWeatherData behaviour.
	 *
	 * @param greenEnergyAgent agent which is executing the behaviour
	 * @param protocol         protocol of the message
	 * @param weatherHandler   function executed when weather data is received from Monitoring Agent (function accepts
	 *                         Monitoring data and optional error that may occur if incorrect message format was received)
	 * @param refusalHandler   function executed when Monitoring agent refuses to retrieve weather data
	 */
	public static RequestWeatherData createWeatherRequest(final GreenEnergyAgent greenEnergyAgent,
			final String protocol, final BiConsumer<MonitoringData, Exception> weatherHandler,
			final Runnable refusalHandler) {
		return createWeatherRequest(greenEnergyAgent, protocol, protocol, weatherHandler, refusalHandler, null);
	}

	/**
	 * Method creates the RequestWeatherData behaviour.
	 *
	 * @param greenEnergyAgent agent which is executing the behaviour
	 * @param protocol         protocol of the message
	 * @param weatherHandler   function executed when weather data is received from Monitoring Agent (function accepts
	 *                         Monitoring data and optional error that may occur if incorrect message format was received)
	 * @param refusalHandler   function executed when Monitoring agent refuses to retrieve weather data
	 * @param serverJob        job for which the weather is to be checked
	 */
	public static RequestWeatherData createWeatherRequest(final GreenEnergyAgent greenEnergyAgent,
			final String protocol, final BiConsumer<MonitoringData, Exception> weatherHandler,
			final Runnable refusalHandler, final ServerJob serverJob) {
		return createWeatherRequest(greenEnergyAgent, protocol, protocol, weatherHandler, refusalHandler, serverJob);
	}

	/**
	 * Method executes REFUSE message handler
	 *
	 * @param refuse refuse response
	 */
	@Override
	protected void handleRefuse(ACLMessage refuse) {
		refusalHandler.run();
	}

	/**
	 * Method executes corresponding handler responsible for processing INFORM message containing weather data
	 *
	 * @param inform message with weather data
	 */
	@Override
	protected void handleInform(ACLMessage inform) {
		try {
			final MonitoringData data = readMessageContent(inform, MonitoringData.class);
			weatherHandler.accept(data, null);
		} catch (IncorrectMessageContentException e) {
			weatherHandler.accept(null, e);
		}
	}

}
