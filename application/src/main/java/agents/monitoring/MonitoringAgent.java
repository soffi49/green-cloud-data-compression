package agents.monitoring;

import static java.time.Instant.now;
import static java.util.Comparator.comparingLong;

import agents.AbstractAgent;
import agents.monitoring.behaviour.ServeForecastInformation;
import agents.monitoring.behaviour.ServeWeatherInformation;
import common.behaviours.ReceiveGUIController;
import domain.GreenSourceForecastData;
import domain.GreenSourceWeatherData;
import domain.ImmutableMonitoringData;
import domain.ImmutableWeatherData;
import domain.MonitoringData;
import domain.WeatherData;
import domain.location.Location;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weather.api.OpenWeatherMapApi;
import weather.cache.WeatherCache;
import weather.domain.AbstractWeather;
import weather.domain.FutureWeather;

/**
 * Agent which is responsible for monitoring the weather and sending the data to the Green Source Agent
 */
public class MonitoringAgent extends AbstractAgent {

	private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

	private OpenWeatherMapApi api;
	private WeatherCache cache;

	public MonitoringAgent() {
		super.setup();
	}

	/**
	 * Method run at the agent start. It starts the behaviour which is listening for the weather requests.
	 */
	@Override
	protected void setup() {
		api = new OpenWeatherMapApi();
		addBehaviour(new ReceiveGUIController(this, List.of(
				new ServeForecastInformation(this),
				new ServeWeatherInformation(this)
		)));
		cache = WeatherCache.getInstance();
	}

	/**
	 * Method which runs when the agent is being deleted. It logs the information to the console.
	 */
	@Override
	protected void takeDown() {
		logger.info("I'm finished. Bye!");
		getGuiController().removeAgentNodeFromGraph(getAgentNode());
		super.takeDown();
	}

	public MonitoringData getWeather(GreenSourceWeatherData requestData) {
		logger.info("Retrieving weather info for {}!", requestData.getLocation());
		return ImmutableMonitoringData.builder()
				.addWeatherData(getWeatherData(requestData.getLocation(), now()))
				.build();
	}

	public MonitoringData getForecast(GreenSourceForecastData requestData) {
		var location = requestData.getLocation();
		logger.info("Retrieving forecast info for {}!", location);
		var weatherData = requestData.getTimetable().stream()
				.map(time -> getForecastData(location, time))
				.toList();
		return ImmutableMonitoringData.builder()
				.weatherData(weatherData)
				.build();
	}

	private WeatherData getWeatherData(Location location, Instant time) {
		return cache.getForecast(location, time).map(f -> buildWeatherData(f, time)).orElseGet(() -> {
					var weather = api.getWeather(location);
					cache.updateCache(location, weather);
					return buildWeatherData(weather, weather.getTimestamp());
				}
		);
	}

	private WeatherData getForecastData(Location location, Instant time) {
		return cache.getForecast(location, time).map(f -> buildWeatherData(f, time)).orElseGet(() -> {
					var forecast = api.getForecast(location);
					cache.updateCache(location, forecast);
					return buildWeatherData(getNearestForecast(forecast.getList(), time), time);
				}
		);
	}

	private WeatherData buildWeatherData(AbstractWeather weather, Instant timestamp) {
		return ImmutableWeatherData.builder()
				.temperature(weather.getMain().getTemp())
				.cloudCover(weather.getClouds().getAll())
				.windSpeed(weather.getWind().getSpeed())
				.time(timestamp)
				.build();
	}

	private FutureWeather getNearestForecast(List<FutureWeather> forecasts, Instant timestamp) {
		var timestamps = forecasts.stream().map(FutureWeather::getTimestamp).toList();
		var nearestTimestamp = timestamps.stream()
				.min(comparingLong(i -> Math.abs(i.getEpochSecond() - timestamp.getEpochSecond())))
				.orElseThrow(() -> new NoSuchElementException("No value present"));
		return forecasts.stream()
				.filter(forecast -> forecast.getTimestamp().equals(nearestTimestamp))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("No value present"));
	}
}
