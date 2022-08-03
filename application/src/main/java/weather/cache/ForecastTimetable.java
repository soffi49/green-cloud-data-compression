package weather.cache;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Comparator.comparingLong;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weather.domain.AbstractWeather;
import weather.domain.CurrentWeather;
import weather.domain.Forecast;

public class ForecastTimetable {

	private static final Logger logger = LoggerFactory.getLogger(ForecastTimetable.class);

	private static final Duration EXPIRATION_TIME = Duration.of(4, HOURS);

	private final Map<Instant, AbstractWeather> timetable;

	public ForecastTimetable(Forecast forecast) {
		timetable = new TreeMap<>();
		forecast.getList().forEach(weather -> timetable.put(weather.getTimestamp(), weather));
	}

	public ForecastTimetable(CurrentWeather weather) {
		timetable = new TreeMap<>();
		timetable.put(weather.getTimestamp(), weather);
	}

	public Map<Instant, AbstractWeather> getTimetable() {
		return timetable;
	}

	public void updateTimetable(Forecast forecast) {
		forecast.getList().forEach(weather -> timetable.put(weather.getTimestamp(), weather));
	}

	public void updateTimetable(CurrentWeather weather) {
		timetable.put(weather.getTimestamp(), weather);
	}

	public Optional<AbstractWeather> getFutureWeather(Instant timestamp) {
		var nearestTimestamp = timetable.keySet().stream()
				.min(comparingLong(i -> Math.abs(i.getEpochSecond() - timestamp.getEpochSecond())))
				.orElseThrow(() -> new NoSuchElementException("No value present"));

		if (Duration.between(timestamp, nearestTimestamp).compareTo(EXPIRATION_TIME) > 0) {
			logger.debug("Stale forecast timetable!");
			return Optional.empty();
		}

		return Optional.of(timetable.get(nearestTimestamp));
	}
}
