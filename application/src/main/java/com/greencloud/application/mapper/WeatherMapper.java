package com.greencloud.application.mapper;

import java.time.Instant;

import com.greencloud.application.domain.weather.ImmutableWeatherData;
import com.greencloud.application.domain.weather.WeatherData;
import com.greencloud.application.weather.domain.AbstractWeather;

/**
 * Class provides set of methods mapping weather object classes
 */
public class WeatherMapper {

	/**
	 * @param weather   abstract weather data
	 * @param timestamp time instant
	 * @return WeatherData
	 */
	public static WeatherData mapToWeatherData(final AbstractWeather weather, final Instant timestamp) {
		return ImmutableWeatherData.builder()
				.temperature(weather.getMain().getTemp())
				.cloudCover(weather.getClouds().getAll())
				.windSpeed(weather.getWind().getSpeed())
				.time(timestamp)
				.build();
	}
}
