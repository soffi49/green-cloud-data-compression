package com.greencloud.application.agents.monitoring;

import static com.greencloud.application.agents.monitoring.domain.MonitoringAgentConstants.BAD_STUB_PROBABILITY;
import static java.lang.Double.parseDouble;
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Objects;

import com.greencloud.application.agents.monitoring.behaviour.ServeForecastWeather;

import jade.core.behaviours.Behaviour;

/**
 * Agent representing weather station that is responsible for retrieving the weather and sending the data to the
 * Green Source Agent
 */
public class MonitoringAgent extends AbstractMonitoringAgent {

	@Override
	protected void initializeAgent(final Object[] args) {
		if (args.length > 0 && Objects.nonNull(args[0])) {
			this.badStubProbability = parseDouble(String.valueOf(args[0]));
		} else {
			this.badStubProbability = BAD_STUB_PROBABILITY;
		}
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return singletonList(new ServeForecastWeather(this));
	}
}
