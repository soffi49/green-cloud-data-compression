package agents.monitoring;

import agents.AbstractAgent;
import agents.monitoring.management.MonitoringWeatherManagement;

/**
 * Abstract agent class storing data of the Monitoring Agent
 */
public abstract class AbstractMonitoringAgent extends AbstractAgent {
	private final MonitoringWeatherManagement weatherManagement;

	/**
	 * Default agent constructor
	 *
	 * @apiNote weatherManagement - monitoring agent manager used to perform operations connected with weather
	 */
	AbstractMonitoringAgent() {
		super.setup();
		this.weatherManagement = new MonitoringWeatherManagement();
	}

	public MonitoringWeatherManagement manageWeather() {
		return weatherManagement;
	}
}
