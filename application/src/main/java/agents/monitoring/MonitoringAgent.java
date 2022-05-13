package agents.monitoring;

import agents.monitoring.behaviour.MonitoringAgentReadMessages;
import jade.core.Agent;
import domain.ImmutableMonitoringData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);
    @Override
    protected void setup() {
        super.setup();
        addBehaviour(MonitoringAgentReadMessages.createFor(this));
    }

    @Override
    protected void takeDown(){
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    // Stub metoda. Znalazlem API z pogodą, można później zaimplementować.
    public ImmutableMonitoringData getWeather(){
        logger.info("Retrieving weather info...");
        return ImmutableMonitoringData.builder()
                .temperature(25)
                .cloudCover(0.15)
                .windSpeed(50)
                .build();
    }
}
