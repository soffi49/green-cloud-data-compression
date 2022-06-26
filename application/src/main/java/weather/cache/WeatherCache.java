package weather.cache;

import domain.location.Location;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import weather.domain.Forecast;
import weather.domain.FutureWeather;

public class WeatherCache {

    private static final Map<Location, ForecastTimetable> CACHE = new HashMap<>();
    private static final WeatherCache instance = new WeatherCache();

    private WeatherCache() {
    }

    public static WeatherCache getInstance() {
        return instance;
    }

    public Optional<FutureWeather> getForecast(Location location, Instant timestamp) {
        if (CACHE.containsKey(location)) {
            return CACHE.get(location).getFutureWeather(timestamp);
        }

        return Optional.empty();
    }

    public void updateCache(Location location, Forecast forecast) {
        if (CACHE.containsKey(location)) {
            CACHE.get(location).updateTimetable(forecast);
        } else {
            CACHE.put(location, new ForecastTimetable(forecast));
        }
    }
}
