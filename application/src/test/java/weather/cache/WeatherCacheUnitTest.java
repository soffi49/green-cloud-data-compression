package weather.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static config.constants.CacheTestConstants.MOCK_CLOUD;
import static config.constants.CacheTestConstants.MOCK_CURRENT_WEATHER;
import static config.constants.CacheTestConstants.MOCK_FORECAST;
import static config.constants.CacheTestConstants.MOCK_FUTURE_WEATHER;
import static config.constants.CacheTestConstants.MOCK_LOCATION;
import static config.constants.CacheTestConstants.MOCK_MAIN;
import static config.constants.CacheTestConstants.MOCK_TIME;
import static config.constants.CacheTestConstants.MOCK_WIND;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import domain.location.ImmutableLocation;
import domain.location.Location;
import weather.domain.AbstractWeather;
import weather.domain.CurrentWeather;
import weather.domain.Forecast;
import weather.domain.FutureWeather;
import weather.domain.ImmutableCurrentWeather;
import weather.domain.ImmutableForecast;
import weather.domain.ImmutableFutureWeather;

class WeatherCacheUnitTest {

	private WeatherCache weatherCache;

	@BeforeEach
	void init() {
		weatherCache = WeatherCache.getInstance();
		weatherCache.clearCache();
	}

	@Test
	@DisplayName("Test getting forecast for empty cache")
	void testGetForecastEmptyCache() {
		assertThat(weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME)).isEmpty();
	}

	@Test
	@DisplayName("Test getting forecast for location not present")
	void testGetForecastLocationNotPresent() {
		final Location absentLocation = ImmutableLocation.builder().latitude(0).longitude(0).build();
		weatherCache.updateCache(MOCK_LOCATION, MOCK_FORECAST);
		assertThat(weatherCache.getForecast(absentLocation, MOCK_TIME)).isEmpty();
	}

	@Test
	@DisplayName("Test getting forecast for timestamp not present")
	void testGetForecastTimestampNotPresent() {
		final Instant newTimeStamp = Instant.parse("2022-10-01T11:00:00.000Z");
		weatherCache.updateCache(MOCK_LOCATION, MOCK_FORECAST);
		assertThat(weatherCache.getForecast(MOCK_LOCATION, newTimeStamp)).isEmpty();
	}

	@Test
	@DisplayName("Test getting forecast for correct location and timestamp")
	void testGetForecastCorrectData() {
		weatherCache.updateCache(MOCK_LOCATION, MOCK_FORECAST);

		final Optional<AbstractWeather> afterUpdate = weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME);

		assertThat(afterUpdate).isPresent();
		assertThat(afterUpdate.get().getClouds()).isEqualTo(MOCK_CLOUD);
		assertThat(afterUpdate.get().getMain()).isEqualTo(MOCK_MAIN);
		assertThat(afterUpdate.get().getWind()).isEqualTo(MOCK_WIND);
	}

	@Test
	@DisplayName("Test update cache with forecast for location not present")
	void testUpdateCacheForecastLocationNotPresent() {
		assertThat(weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME)).isEmpty();
		weatherCache.updateCache(MOCK_LOCATION, MOCK_FORECAST);

		final Optional<AbstractWeather> afterUpdate = weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME);

		assertThat(afterUpdate).isPresent();
		assertThat(afterUpdate.get().getClouds()).isEqualTo(MOCK_CLOUD);
		assertThat(afterUpdate.get().getMain()).isEqualTo(MOCK_MAIN);
	}

	@Test
	@DisplayName("Test update cache with forecast for location present")
	void testUpdateCacheForecastLocationPresent() {
		final Instant newTimeStamp = Instant.parse("2022-10-01T11:00:00.000Z");
		final FutureWeather newWeather = ImmutableFutureWeather.copyOf(MOCK_FUTURE_WEATHER).withTimestamp(newTimeStamp);
		final Forecast newForeCast = ImmutableForecast.copyOf(MOCK_FORECAST).withList(newWeather);

		weatherCache.updateCache(MOCK_LOCATION, MOCK_FORECAST);
		assertThat(weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME)).isPresent();
		assertThat(weatherCache.getForecast(MOCK_LOCATION, newTimeStamp)).isEmpty();

		weatherCache.updateCache(MOCK_LOCATION, newForeCast);
		final Optional<AbstractWeather> afterSecondUpdate = weatherCache.getForecast(MOCK_LOCATION, newTimeStamp);

		assertThat(afterSecondUpdate).isPresent();
		assertThat(afterSecondUpdate.get().getClouds()).isEqualTo(MOCK_CLOUD);
		assertThat(afterSecondUpdate.get().getMain()).isEqualTo(MOCK_MAIN);
	}

	@Test
	@DisplayName("Test update cache with current weather for location not present")
	void testUpdateCacheCurrentWeatherLocationNotPresent() {
		assertThat(weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME)).isEmpty();
		weatherCache.updateCache(MOCK_LOCATION, MOCK_CURRENT_WEATHER);

		final Optional<AbstractWeather> afterUpdate = weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME);

		assertThat(afterUpdate).isPresent();
		assertThat(afterUpdate.get().getClouds()).isEqualTo(MOCK_CLOUD);
		assertThat(afterUpdate.get().getMain()).isEqualTo(MOCK_MAIN);
	}

	@Test
	@DisplayName("Test update cache with current weather for location present")
	void testUpdateCacheCurrentWeatherLocationPresent() {
		final Instant newTimeStamp = Instant.parse("2022-10-01T11:00:00.000Z");
		final CurrentWeather newCurrentWeather = ImmutableCurrentWeather.copyOf(MOCK_CURRENT_WEATHER)
				.withTimestamp(newTimeStamp);

		weatherCache.updateCache(MOCK_LOCATION, MOCK_CURRENT_WEATHER);
		assertThat(weatherCache.getForecast(MOCK_LOCATION, MOCK_TIME)).isPresent();
		assertThat(weatherCache.getForecast(MOCK_LOCATION, newTimeStamp)).isEmpty();

		weatherCache.updateCache(MOCK_LOCATION, newCurrentWeather);
		final Optional<AbstractWeather> afterSecondUpdate = weatherCache.getForecast(MOCK_LOCATION, newTimeStamp);

		assertThat(afterSecondUpdate).isPresent();
		assertThat(afterSecondUpdate.get().getClouds()).isEqualTo(MOCK_CLOUD);
		assertThat(afterSecondUpdate.get().getMain()).isEqualTo(MOCK_MAIN);

	}
}
