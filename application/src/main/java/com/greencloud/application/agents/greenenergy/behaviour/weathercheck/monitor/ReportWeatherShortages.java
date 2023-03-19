package com.greencloud.application.agents.greenenergy.behaviour.weathercheck.monitor;

import static com.database.knowledge.domain.agent.DataType.SHORTAGES;
import static com.database.knowledge.domain.agent.DataType.WEATHER_SHORTAGES;
import static com.greencloud.application.agents.greenenergy.constants.GreenEnergyAgentConstants.PERIODIC_SHORTAGE_REPORT_PERIOD;

import com.database.knowledge.domain.agent.greensource.Shortages;
import com.database.knowledge.domain.agent.greensource.WeatherShortages;
import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;

import jade.core.behaviours.TickerBehaviour;

/**
 * Behaviour responsible for periodical reporting the number of weather shortages
 */
public class ReportWeatherShortages extends TickerBehaviour {

	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviour constructor
	 *
	 * @param agent agent executing the behaviour
	 */
	public ReportWeatherShortages(final GreenEnergyAgent agent) {
		super(agent, PERIODIC_SHORTAGE_REPORT_PERIOD);
		myGreenEnergyAgent = agent;
	}

	/**
	 * Method check the number of power fluctuations caused by weather changes (i.e. weatherShortages) and the total
	 * number of power fluctuations caused by all possible factors (i.e. accumulatedShortages).
	 * If the numbers are greater than 0, then the method writes those statistics to the database.
	 */
	@Override
	public void onTick() {
		final int weatherShortages = myGreenEnergyAgent.manage().getWeatherShortagesCounter().get();
		final int accumulatedShortages = myGreenEnergyAgent.manage().getShortagesAccumulator().get();

		if (weatherShortages > 0) {
			myGreenEnergyAgent.manage().getWeatherShortagesCounter().set(0);
			myGreenEnergyAgent.writeMonitoringData(WEATHER_SHORTAGES,
					new WeatherShortages(weatherShortages, PERIODIC_SHORTAGE_REPORT_PERIOD));
		}

		if (accumulatedShortages > 0) {
			myGreenEnergyAgent.writeMonitoringData(SHORTAGES, new Shortages(accumulatedShortages));
		}
	}
}
