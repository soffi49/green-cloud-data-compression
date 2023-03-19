package com.greencloud.application.agents.monitoring;

import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.BAD_STUB_PROBABILITY;
import static com.greencloud.application.domain.agent.enums.AgentManagementEnum.WEATHER_MANAGEMENT;
import static java.lang.Double.parseDouble;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.greencloud.application.agents.monitoring.behaviour.ServeForecastWeather;
import com.greencloud.application.agents.monitoring.management.MonitoringWeatherManagement;

import jade.core.behaviours.Behaviour;

/**
 * Agent representing weather station that is responsible for retrieving the weather and sending the data to the
 * Green Source Agent
 */
public class MonitoringAgent extends AbstractMonitoringAgent {

	@Override
	protected void initializeAgent(final Object[] args) {
		if (args.length > 0 && nonNull(args[0])) {
			this.badStubProbability = parseDouble(String.valueOf(args[0]));
		} else {
			this.badStubProbability = BAD_STUB_PROBABILITY;
		}

		this.agentManagementServices = new EnumMap<>(Map.of(
				WEATHER_MANAGEMENT, new MonitoringWeatherManagement()
		));
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return singletonList(new ServeForecastWeather(this));
	}
}
