package com.greencloud.application.agents.greenenergy.management;

import static com.greencloud.application.constants.CacheTestConstants.MOCK_LOCATION;
import static com.greencloud.application.constants.CacheTestConstants.MOCK_TIME;
import static com.greencloud.commons.agent.greenenergy.GreenEnergySourceTypeEnum.SOLAR;
import static com.greencloud.commons.agent.greenenergy.GreenEnergySourceTypeEnum.WIND;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.domain.weather.ImmutableMonitoringData;
import com.greencloud.application.domain.weather.ImmutableWeatherData;
import com.greencloud.application.domain.weather.MonitoringData;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class GreenPowerManagementUnitTest {

	// MOCK OBJECTS
	public static final MonitoringData MOCK_WEATHER = ImmutableMonitoringData.builder()
			.addWeatherData(ImmutableWeatherData.builder()
					.time(MOCK_TIME)
					.cloudCover(5.0)
					.windSpeed(10.0)
					.temperature(20.0)
					.build())
			.build();

	@Mock
	private GreenEnergyAgent greenEnergyAgent;

	private GreenPowerManagement greenPowerManagement;

	@BeforeEach
	void init() {
		doReturn(MOCK_LOCATION).when(greenEnergyAgent).getLocation();
		greenPowerManagement = new GreenPowerManagement(greenEnergyAgent);
	}

	@Test
	@DisplayName("Test get available power for wind source")
	void testGetAvailablePowerWindSource() {
		doReturn(WIND).when(greenEnergyAgent).getEnergyType();
		final Optional<Double> result = greenPowerManagement.getAvailablePower(MOCK_TIME, MOCK_WEATHER);

		assertThat(result).contains(200D);
	}

	@Test
	@DisplayName("Test get available power for solar source")
	void testGetAvailablePowerSolarSource() {
		doReturn(SOLAR).when(greenEnergyAgent).getEnergyType();
		final Optional<Double> result = greenPowerManagement.getAvailablePower(MOCK_TIME, MOCK_WEATHER);

		assertThat(result).contains(30D);
	}

	@Test
	@DisplayName("Test get available power for solar source during night-time")
	void testGetAvailablePowerSolarSourceNightTime() {
		doReturn(SOLAR).when(greenEnergyAgent).getEnergyType();
		final Instant testTime = parse("2022-01-01T00:00:00.000Z");
		final Optional<Double> result = greenPowerManagement.getAvailablePower(testTime, MOCK_WEATHER);

		assertThat(result).contains(0D);
	}

	@Test
	@DisplayName("Test get available power from monitoring data for nearest time")
	void testGetAvailablePowerMonitoringDataNearestTime() {
		doReturn(WIND).when(greenEnergyAgent).getEnergyType();
		final Instant time1 = parse("2022-01-01T10:00:00.000Z");
		final Instant time2 = parse("2022-01-01T13:00:00.000Z");
		final MonitoringData mockMonitoringData = ImmutableMonitoringData.builder()
				.addWeatherData(ImmutableWeatherData.builder()
						.cloudCover(5.0)
						.windSpeed(10.0)
						.temperature(20.0)
						.windSpeed(200.0)
						.time(time1)
						.build())
				.addWeatherData(ImmutableWeatherData.builder()
						.cloudCover(5.0)
						.windSpeed(10.0)
						.temperature(20.0)
						.windSpeed(100.0)
						.time(time2)
						.build())
				.build();

		final Optional<Double> result = greenPowerManagement.getAvailablePower(MOCK_TIME, mockMonitoringData);
		assertThat(result).contains(200D);
	}

	@Test
	@DisplayName("Test set new maximum capacity")
	void testSetNewMaximumCapacity() {
		assertThat(greenEnergyAgent.getCurrentMaximumCapacity()).isZero();
		greenEnergyAgent.setCurrentMaximumCapacity(100);
		assertThat(greenEnergyAgent.getCurrentMaximumCapacity()).isEqualTo(100);
	}

	@Test
	@DisplayName("Test get initial maximum capacity")
	void testGetMaximumCapacity() {
		assertThat(greenEnergyAgent.getInitialMaximumCapacity()).isZero();
		greenEnergyAgent.setCurrentMaximumCapacity(100);
		assertThat(greenEnergyAgent.getInitialMaximumCapacity()).isZero();
	}
}
