package com.greencloud.application.agents.monitoring.management;

import static com.greencloud.application.agents.monitoring.management.logs.MonitoringManagementLog.RETRIEVE_WEATHER_LOG;
import static com.greencloud.application.mapper.WeatherMapper.mapToWeatherData;
import static com.greencloud.application.utils.TimeUtils.convertToRealTime;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static java.lang.Math.abs;
import static java.util.Comparator.comparingLong;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;

import com.greencloud.application.agents.AbstractAgentManagement;
import com.greencloud.application.domain.agent.GreenSourceForecastData;
import com.greencloud.application.domain.agent.GreenSourceWeatherData;
import com.greencloud.application.domain.weather.ImmutableMonitoringData;
import com.greencloud.application.domain.weather.MonitoringData;
import com.greencloud.application.domain.weather.WeatherData;
import com.greencloud.application.exception.APIFetchInternalException;
import com.greencloud.application.weather.api.OpenWeatherMapApi;
import com.greencloud.application.weather.cache.WeatherCache;
import com.greencloud.application.weather.domain.CurrentWeather;
import com.greencloud.application.weather.domain.Forecast;
import com.greencloud.application.weather.domain.FutureWeather;
import com.greencloud.commons.domain.location.Location;

/**
 * Set of methods used in weather management
 */
public class MonitoringWeatherManagement extends AbstractAgentManagement implements Serializable {

	private static final Logger logger = getLogger(MonitoringWeatherManagement.class);

	private final transient OpenWeatherMapApi api;
	private final transient WeatherCache cache;

	/**
	 * Default constructor
	 */
	public MonitoringWeatherManagement() {
		this.api = new OpenWeatherMapApi();
		this.cache = WeatherCache.getInstance();
	}

	/**
	 * Constructor initializing api and cache for weather
	 *
	 * @param api   weather api
	 * @param cache weather cache
	 */
	public MonitoringWeatherManagement(final OpenWeatherMapApi api, final WeatherCache cache) {
		this.api = api;
		this.cache = cache;
	}

	/**
	 * Method retrieves weather for given location at current moment
	 *
	 * @param data information about location
	 * @return weather for current moment
	 */
	public MonitoringData getWeather(final GreenSourceWeatherData data) {
		logger.info(RETRIEVE_WEATHER_LOG, data.getLocation());
		return ImmutableMonitoringData.builder()
				.addWeatherData(getWeatherData(data.getLocation(), convertToRealTime(getCurrentTime())))
				.build();
	}

	/**
	 * Method retrieves weather for given location at given time
	 *
	 * @param requestData information about location and time
	 * @return weather for given time
	 */
	public MonitoringData getForecast(final GreenSourceForecastData requestData) {
		final Location location = requestData.getLocation();
		logger.info(RETRIEVE_WEATHER_LOG, location);
		final List<WeatherData> weatherData = requestData.getTimetable().stream()
				.map(time -> getForecastData(location, time))
				.toList();
		return new ImmutableMonitoringData(weatherData);
	}

	private WeatherData getWeatherData(final Location location, final Instant time) {
		return cache.getForecast(location, time)
				.map(forecast -> mapToWeatherData(forecast, time))
				.orElseGet(() -> {
							final CurrentWeather weather = api.getWeather(location);
							if (nonNull(weather)) {
								cache.updateCache(location, weather);
								return mapToWeatherData(weather, weather.getTimestamp());
							} else {
								throw new APIFetchInternalException();
							}
						}
				);
	}

	private WeatherData getForecastData(Location location, Instant time) {
		return cache.getForecast(location, time)
				.map(forecast -> mapToWeatherData(forecast, time))
				.orElseGet(() -> {
							final Forecast forecast = api.getForecast(location);
							if (nonNull(forecast)) {
								cache.updateCache(location, forecast);
								return mapToWeatherData(getNearestForecast(forecast.getList(), time), time);
							} else {
								throw new APIFetchInternalException();
							}
						}
				);
	}

	private FutureWeather getNearestForecast(final List<FutureWeather> forecasts, final Instant timestamp) {
		return forecasts.stream()
				.min(comparingLong(i -> abs(i.getTimestamp().getEpochSecond() - timestamp.getEpochSecond())))
				.orElseThrow(() -> new NoSuchElementException("No value present"));
	}
}
