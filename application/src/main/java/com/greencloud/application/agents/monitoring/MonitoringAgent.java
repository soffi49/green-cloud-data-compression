package com.greencloud.application.agents.monitoring;

import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.BAD_STUB_PROBABILITY;
import static com.greencloud.application.domain.agent.enums.AgentManagementEnum.WEATHER_MANAGEMENT;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

import com.greencloud.application.agents.monitoring.behaviour.ServeForecastWeather;
import com.greencloud.application.agents.monitoring.management.MonitoringWeatherManagement;

import jade.core.behaviours.Behaviour;

/**
 * Agent representing weather station that is responsible for retrieving the weather and sending the data to the
 * Green Source Agent
 */
public class MonitoringAgent extends AbstractMonitoringAgent {
	private static final Logger logger = getLogger(MonitoringAgent.class);

	@Override
	protected void initializeAgent(final Object[] args) {
		if (args.length > 2 && nonNull(args[0])) {
			this.badStubProbability = parseDouble(String.valueOf(args[0]));
		} else {
			this.badStubProbability = BAD_STUB_PROBABILITY;
		}
		readOfflineMode();
	}

	/**
	 * Abstract method used to initialize agent management services
	 */
	@Override
	protected void initializeAgentManagements() {
		this.agentManagementServices = new EnumMap<>(Map.of(
				WEATHER_MANAGEMENT, new MonitoringWeatherManagement()
		));
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return singletonList(new ServeForecastWeather(this));
	}

	private void readOfflineMode() {
		final Properties properties = new Properties();

		try (final InputStream res = getClass().getClassLoader().getResourceAsStream("config.properties")) {
			properties.load(res);
			this.offlineMode = parseBoolean(properties.getProperty("offline.mode"));
		} catch (Exception exception) {
			logger.warn("Could not load properties file {}. Setting offline mode to false.", exception.toString());
			this.offlineMode = false;
		}
	}
}
