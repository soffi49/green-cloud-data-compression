package com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.logs;

/**
 * Class contains all constants dedicated to logging messages in com.greencloud.application.weather checking request behaviours
 */
public class WeatherCheckRequestLog {

	// WEATHER REQUEST LOG MESSAGES
	public static final String WEATHER_REQUEST_SENT_LOG = "Sending request for weather to Monitoring Agent";

	// PERIODIC WEATHER CHECK LOG MESSAGES
	public static final String PERIODIC_CHECK_SENT_LOG = "Checking the current weather";
	public static final String POWER_DROP_LOG =
			"Received the weather data at {}. There was a power drop! Scheduling job transferring behaviour!";
	public static final String NO_POWER_DROP_LOG =
			"Received the weather data at {}. Power has not dropped. Continuing jobs execution";
	public static final String WEATHER_UNAVAILABLE_LOG = "The weather data is not available at {}";
}
