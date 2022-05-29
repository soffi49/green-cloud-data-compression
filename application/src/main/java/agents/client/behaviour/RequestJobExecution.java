package agents.client.behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.Vector;

/**
 * Behaviour responsible for sending and handling job's call for proposal
 * (instead of SendJobCallForProposal and HandleCNACallForProposal behaviours)
 */
public class RequestJobExecution extends ContractNetInitiator {

    public RequestJobExecution(Agent a, ACLMessage cfp) {
        super(a, cfp);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected Vector prepareCfps(ACLMessage callForProposal) {
        var vector = new Vector<ACLMessage>();
        // find cloud network agents and add them to message
        vector.add(callForProposal);
        return vector;
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
