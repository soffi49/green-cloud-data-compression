package agents.monitoring;

import agents.AbstractAgent;
import agents.monitoring.behaviour.ServeWeatherInformation;
import behaviours.ReceiveGUIController;
import domain.GreenSourceRequestData;
import domain.ImmutableMonitoringData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weather.api.OpenWeatherMapApi;

import java.util.List;

/**
 * Agent which is responsible for monitoring the weather and sending the data to the Green Source Agent
 */
public class MonitoringAgent extends AbstractAgent {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    private OpenWeatherMapApi api;

    public MonitoringAgent() {
        super.setup();
    }

    /**
     * Method run at the agent start. It starts the behaviour which is listening for the weather requests.
     */
    @Override
    protected void setup() {
        api = new OpenWeatherMapApi();
        addBehaviour(new ReceiveGUIController(this, List.of(new ServeWeatherInformation(this))));
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
