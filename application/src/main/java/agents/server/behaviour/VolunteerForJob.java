package agents.server.behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

/**
 * Behaviours responsible for sending volunteering offer to CNA
 * (instead of HandleCNAAcceptProposal and HandleCNARejectProposal)
 */
public class VolunteerForJob extends ProposeInitiator {

    public VolunteerForJob(Agent a, ACLMessage msg) {
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
