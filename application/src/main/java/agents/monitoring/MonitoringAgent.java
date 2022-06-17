package agents.monitoring;

import agents.monitoring.behaviour.ServeWeatherInformation;
import domain.ImmutableMonitoringData;
import domain.ServerRequestData;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Agent which is responsible for monitoring the weather and sending the data to the Green Source Agent
 */
public class MonitoringAgent extends Agent {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    /**
     * Method run at the agent start. It starts the behaviour which is listening for the weather requests.
     */
    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new ServeWeatherInformation(this));
    }

    /**
     * Method which runs when the agent is being deleted. It logs the information to the console.
     */
    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    // Stub metoda. Znalazlem API z pogodą, można później zaimplementować.
    public ImmutableMonitoringData getWeather(ServerRequestData requestData) {
        logger.info("Retrieving weather info for {}...", requestData.getLocation());
        return ImmutableMonitoringData.builder()
            .temperature(25)
            .cloudCover(0.15)
            .windSpeed(50)
            .build();
    }
}
