package agents.monitoring.domain;

import static utils.TimeUtils.getCurrentTime;

import java.time.Instant;

import domain.ImmutableMonitoringData;
import domain.ImmutableWeatherData;
import domain.MonitoringData;

/**
 * Class stores all predefined constants for Monitoring Agent
 */
public class MonitoringAgentConstants {

	public static final boolean OFFLINE_MODE = true;
	public static final double BAD_STUB_PROBABILITY = 0.5;
	public static final MonitoringData STUB_DATA =
			ImmutableMonitoringData.builder()
					.addWeatherData(ImmutableWeatherData.builder()
							.cloudCover(25.0)
							.temperature(25.0)
							.windSpeed(10.0)
							.time(Instant.now())
							.build())
					.build();
	public static final MonitoringData BAD_STUB_DATA =
			ImmutableMonitoringData.builder()
					.addWeatherData(ImmutableWeatherData.builder()
							.cloudCover(50.0)
							.temperature(10.0)
							.windSpeed(5.0)
							.time(getCurrentTime())
							.build())
					.build();
}
