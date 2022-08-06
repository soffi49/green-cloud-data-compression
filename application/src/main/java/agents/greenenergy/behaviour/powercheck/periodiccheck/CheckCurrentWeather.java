package agents.greenenergy.behaviour.powercheck.periodiccheck;

import static agents.greenenergy.domain.GreenEnergyAgentConstants.PERIODIC_WEATHER_CHECK_TIMEOUT;
import static common.constant.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powercheck.jobstart.ReceiveWeatherDataForJobStart;
import agents.greenenergy.behaviour.powercheck.RequestWeatherData;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for checking the current weather and evaluating if the power drop has happened
 */
public class CheckCurrentWeather extends TickerBehaviour {


    private static final Logger logger = LoggerFactory.getLogger(ReceivePeriodicWeatherData.class);

    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor
     *
     * @param agent
     */
    public CheckCurrentWeather(GreenEnergyAgent agent) {
        super(agent, PERIODIC_WEATHER_CHECK_TIMEOUT);
        this.myGreenEnergyAgent = agent;
    }

    @Override
    protected void onTick() {
        logger.info("[{}] Checking the current weather", myGreenEnergyAgent.getName());
        final SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new RequestWeatherData(myGreenEnergyAgent, PERIODIC_WEATHER_CHECK_PROTOCOL, PERIODIC_WEATHER_CHECK_PROTOCOL));
        sequentialBehaviour.addSubBehaviour(new ReceivePeriodicWeatherData(myGreenEnergyAgent, sequentialBehaviour));
        myAgent.addBehaviour(sequentialBehaviour);
    }
}
