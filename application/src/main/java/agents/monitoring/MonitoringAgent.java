package agents.monitoring;

import static java.util.Comparator.comparingLong;

import agents.monitoring.behaviour.ServeWeatherInformation;
import domain.GreenSourceRequestData;
import domain.ImmutableMonitoringData;
import domain.MonitoringData;
import jade.core.Agent;
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
public class MonitoringAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    private OpenWeatherMapApi api;
    private WeatherCache cache;

    /**
     * Method run at the agent start. It starts the behaviour which is listening for the weather requests.
     */
    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new ServeWeatherInformation(this));
        api = new OpenWeatherMapApi();
        cache = WeatherCache.getInstance();
    }

    /**
     * Method which runs when the agent is being deleted. It logs the information to the console.
     */
    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    public MonitoringData getWeather(GreenSourceRequestData requestData) {
        logger.info("Retrieving weather info for {}...", requestData.getLocation());
        var weather = api.getWeather(requestData.getLocation());
        return buildMonitoringData(weather);
    }

    public MonitoringData getForecast(GreenSourceRequestData requestData) {
        var location = requestData.getLocation();
        var timestamp = requestData.getStartDate().toInstant();
        logger.info("Retrieving forecast info for {} at {}...", location, timestamp);
        return cache.getForecast(location, timestamp).map(this::buildMonitoringData).orElseGet(() -> {
            var forecast = api.getForecast(location);
            cache.updateCache(location, forecast);
            return buildMonitoringData(getNearestForecast(forecast.getList(), timestamp));
        });
    }

    private MonitoringData buildMonitoringData(AbstractWeather weather) {
        return ImmutableMonitoringData.builder()
            .temperature(weather.getMain().getTemp())
            .cloudCover(weather.getClouds().getAll())
            .windSpeed(weather.getWind().getSpeed())
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
