package agents.monitoring;

import agents.monitoring.behaviour.ServeWeatherInformation;
import domain.ImmutableMonitoringData;
import domain.ServerRequestData;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weather.api.OpenWeatherMapApi;

public class MonitoringAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    private OpenWeatherMapApi api;

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(ServeWeatherInformation.createFor(this));
        api = new OpenWeatherMapApi();
    }

    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    public ImmutableMonitoringData getWeather(ServerRequestData requestData) {
        logger.info("Retrieving weather info for {}...", requestData.getLocation());
        var weather = api.getWeather(requestData.getLocation());
        return ImmutableMonitoringData.builder()
            .job(requestData.getJob())
            .temperature(weather.getMain().getTemp())
            .cloudCover(weather.getClouds().getAll())
            .windSpeed(weather.getWind().getSpeed())
            .build();
    }
}
