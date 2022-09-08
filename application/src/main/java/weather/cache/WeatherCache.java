package weather.cache;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import domain.location.Location;
import weather.domain.AbstractWeather;
import weather.domain.CurrentWeather;
import weather.domain.Forecast;

/**
 * Class represents a weather cache storing weather forecast for given location
 */
public class WeatherCache {

	private static final Map<Location, ForecastTimetable> CACHE = new HashMap<>();
	private static final WeatherCache instance = new WeatherCache();

	private WeatherCache() {
	}

	public static WeatherCache getInstance() {
		return instance;
	}

	/**
	 * Method retrieves weather forecast for given location and timestamp
	 *
	 * @param location  location for which the weather is to be retrieved
	 * @param timestamp time for which the weather is to be retrieved
	 * @return AbstractWeather forecast
	 */
	public Optional<AbstractWeather> getForecast(Location location, Instant timestamp) {
		if (CACHE.containsKey(location)) {
			return CACHE.get(location).getFutureWeather(timestamp);
		}
		return Optional.empty();
	}

	/**
	 * Method updates the cache with given forecast
	 *
	 * @param location location for given forecast
	 * @param forecast weather forecast
	 */
	public void updateCache(Location location, Forecast forecast) {
		if (CACHE.containsKey(location)) {
			CACHE.get(location).updateTimetable(forecast);
		} else {
			CACHE.put(location, new ForecastTimetable(forecast));
		}
	}

	/**
	 * Method updates the cache with given current weather
	 *
	 * @param location       location for given weather
	 * @param currentWeather weather
	 */
	public void updateCache(Location location, CurrentWeather currentWeather) {
		if (CACHE.containsKey(location)) {
			CACHE.get(location).updateTimetable(currentWeather);
		} else {
			CACHE.put(location, new ForecastTimetable(currentWeather));
		}
	}

	/**
	 * Method clears the current cache (for testing purposes)
	 */
	public void clearCache() {
		CACHE.clear();
	}
}
