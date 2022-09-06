package agents.monitoring.management;

import static java.time.Instant.now;
import static java.util.Comparator.comparingLong;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import domain.GreenSourceForecastData;
import domain.GreenSourceWeatherData;
import domain.ImmutableMonitoringData;
import domain.ImmutableWeatherData;
import domain.MonitoringData;
import domain.WeatherData;
import domain.location.Location;
import weather.api.OpenWeatherMapApi;
import weather.cache.WeatherCache;
import weather.domain.AbstractWeather;
import weather.domain.FutureWeather;

/**
 * Class stores all methods used in weather management
 */
public class MonitoringWeatherManagement {

	private static final Logger logger = LoggerFactory.getLogger(MonitoringWeatherManagement.class);
	private final OpenWeatherMapApi api;
	private final WeatherCache cache;

	/**
	 * Constructor initializing api and cache for weather
	 */
	public MonitoringWeatherManagement() {
		this.api = new OpenWeatherMapApi();
		this.cache = WeatherCache.getInstance();
	}

	/**
	 * Method retrieves weather for given location at current moment
	 *
	 * @param requestData information about location
	 * @return weather for current moment
	 */
	public MonitoringData getWeather(GreenSourceWeatherData requestData) {
		logger.info("Retrieving weather info for {}!", requestData.getLocation());
		return ImmutableMonitoringData.builder()
				.addWeatherData(getWeatherData(requestData.getLocation(), now()))
				.build();
	}

	/**
	 * Method retrieves weather for given location at given time
	 *
	 * @param requestData information about location and time
	 * @return weather for given time
	 */
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
