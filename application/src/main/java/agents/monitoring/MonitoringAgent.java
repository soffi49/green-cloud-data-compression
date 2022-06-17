package agents.monitoring;

import agents.monitoring.behaviour.ServeWeatherInformation;
import domain.ImmutableMonitoringData;
import domain.GreenSourceRequestData;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weather.api.OpenWeatherMapApi;

/**
 * Agent which is responsible for monitoring the weather and sending the data to the Green Source Agent
 */
public class MonitoringAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    private OpenWeatherMapApi api;

    /**
     * Method run at the agent start. It starts the behaviour which is listening for the weather requests.
     */
    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new ServeWeatherInformation(this));
        api = new OpenWeatherMapApi();
    }

    /**
     * Method which runs when the agent is being deleted. It logs the information to the console.
     */
    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    public ImmutableMonitoringData getWeather(GreenSourceRequestData requestData) {
        logger.info("Retrieving weather info for {}...", requestData.getLocation());
        var weather = api.getWeather(requestData.getLocation());
        return ImmutableMonitoringData.builder()
            .temperature(weather.getMain().getTemp())
            .cloudCover(weather.getClouds().getAll())
            .windSpeed(weather.getWind().getSpeed())
            .build();
    }
}
