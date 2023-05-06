package com.greencloud.application.agents.monitoring;

import static com.greencloud.application.domain.agent.enums.AgentManagementEnum.WEATHER_MANAGEMENT;
import static com.greencloud.commons.agent.AgentType.MONITORING;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.application.agents.monitoring.management.MonitoringWeatherManagement;

/**
 * Abstract agent class storing data of the Monitoring Agent
 */
public abstract class AbstractMonitoringAgent extends AbstractAgent {

	protected double badStubProbability;

	/**
	 * Default agent constructor
	 *
	 * @apiNote weatherManagement - monitoring agent manager used to perform operations connected with weather
	 */
	AbstractMonitoringAgent() {
		super();
		agentType = MONITORING;
	}

	public MonitoringWeatherManagement manageWeather() {
		return (MonitoringWeatherManagement) agentManagementServices.get(WEATHER_MANAGEMENT);
	}

	public double getBadStubProbability() {
		return badStubProbability;
	}
}
