package agents.server.behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.Vector;

/**
 * Behaviours responsible for passing the job/power request to green sources and choosing one to provide power
 * (instead of HandleGreenSourceCallForProposalResponse and HandleGreenSourceJobInform)
 */
public class AnnouncePowerRequest extends ContractNetInitiator {

    public AnnouncePowerRequest(Agent a, ACLMessage receivedJob) {
        super(a, receivedJob);
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        super.handleAllResponses(responses, acceptances);
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        super.handleInform(inform);
    }
}
