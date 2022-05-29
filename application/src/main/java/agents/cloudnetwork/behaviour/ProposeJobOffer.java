package agents.cloudnetwork.behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

/**
 * Behaviour responsible for sending proposal with job execution offer to the client
 * (instead of HandleClientRejectJobProposal and HandleClientAcceptJobProposal)
 */
public class ProposeJobOffer extends ProposeInitiator {

    public ProposeJobOffer(Agent a, ACLMessage msg) {
        super(a, msg);
    }

    @Override
    protected void handleAcceptProposal(ACLMessage accept_proposal) {
        super.handleAcceptProposal(accept_proposal);
    }

    @Override
    protected void handleRejectProposal(ACLMessage reject_proposal) {
        super.handleRejectProposal(reject_proposal);
    }
}
