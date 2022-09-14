package com.greencloud.application.agents.monitoring;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.monitoring.behaviour.ServeForecastWeather;
import com.greencloud.application.behaviours.ReceiveGUIController;

/**
 * Agent which is responsible for monitoring the com.greencloud.application.weather and sending the data to the Green Source Agent
 */
public class MonitoringAgent extends AbstractMonitoringAgent {

	private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

	/**
	 * Method starts the behaviour which is listening for the com.greencloud.application.weather requests.
	 */
	@Override
	protected void setup() {
		super.setup();
		addBehaviour(new ReceiveGUIController(this, Collections.singletonList(new ServeForecastWeather(this))));
	}

	/**
	 * Method logs the information to the console.
	 */
	@Override
	protected void takeDown() {
		logger.info("I'm finished. Bye!");
		getGuiController().removeAgentNodeFromGraph(getAgentNode());
		super.takeDown();
	}
}
