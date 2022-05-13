package agents.greenenergy.behaviour;

import agents.greenenergy.GreenEnergyAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.PROPOSE;

public class GreenEnergyAgentReadMessages extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(GreenEnergyAgentReadMessages.class);

    private GreenEnergyAgent greenEnergyAgent;

    public GreenEnergyAgentReadMessages(GreenEnergyAgent greenEnergyAgent){
        this.greenEnergyAgent = greenEnergyAgent;
    }

    public static GreenEnergyAgentReadMessages createFor(GreenEnergyAgent greenEnergyAgent){
        return new GreenEnergyAgentReadMessages(greenEnergyAgent);
    }

    @Override
    public void action(){
        final ACLMessage message = greenEnergyAgent.receive();
        if(Objects.nonNull(message)){
            switch (message.getPerformative()){
                case PROPOSE:
                    break;
                case INFORM:
                    break;
            }
        }
        else{
            block();
        }
    }
}
