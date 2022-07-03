package agents.greenenergy.behaviour;

import static common.constant.DFServiceConstants.GS_SERVICE_NAME;
import static common.constant.DFServiceConstants.GS_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;
import agents.greenenergy.GreenEnergyAgent;
import jade.core.behaviours.TickerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behavior that registers green energy agent in the directory facilitator every 2 hours
 */
public class RegisterGreenAgentInDF extends TickerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveWeatherData.class);

    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor.
     *
     * @param myAgent agent which is executing the behaviour
     * @param period  period described in milliseconds to register the agent
     */
    public RegisterGreenAgentInDF(GreenEnergyAgent myAgent, long period){
        super(myAgent, period);
        this.myGreenEnergyAgent = myAgent;
    }

    @Override
    public void onStart(){
        //register(myGreenEnergyAgent, GS_SERVICE_TYPE, GS_SERVICE_NAME, myGreenEnergyAgent.getOwnerServer().getName());
    }
    /**
     * Method registering the agent in the df each period
     */
    @Override
    protected void onTick() {
        register(myGreenEnergyAgent, GS_SERVICE_TYPE, GS_SERVICE_NAME, myGreenEnergyAgent.getOwnerServer().getName());
    }
}
