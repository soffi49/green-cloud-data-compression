package agents.greenenergy.behaviour;

import static common.constant.DFServiceConstants.GS_SERVICE_NAME;
import static common.constant.DFServiceConstants.GS_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.server.ServerAgent;
import jade.core.behaviours.TickerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behavior that registers green energy agent in the directory facilitator every 2 hours
 */
public class RegisterServerInDF extends TickerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveWeatherData.class);

    private final ServerAgent myServerAgent;

    /**
     * Behaviour constructor.
     *
     * @param myAgent agent which is executing the behaviour
     * @param period  period described in milliseconds to register the agent
     */
    public RegisterServerInDF(ServerAgent myAgent, long period){
        super(myAgent, period);
        this.myServerAgent = myAgent;
    }

    @Override
    public void onStart(){
        //register(myServerAgent, GS_SERVICE_TYPE, GS_SERVICE_NAME, myServerAgent.getOwnerCloudNetworkAgent().getName());
    }
    /**
     * Method registering the agent in the df each period
     */
    @Override
    protected void onTick() {
        register(myServerAgent, GS_SERVICE_TYPE, GS_SERVICE_NAME, myServerAgent.getOwnerCloudNetworkAgent().getName());
    }
}
