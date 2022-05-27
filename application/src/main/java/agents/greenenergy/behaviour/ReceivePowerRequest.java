package agents.greenenergy.behaviour;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

/**
 * Behaviour responsible for handling server call for proposal for given job
 * (instead of HandleServerCallForProposal, HandleServerAcceptProposal, HandleServerRejectProposal)
 */
public class ReceivePowerRequest extends ContractNetResponder {

    public ReceivePowerRequest(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
        return super.handleCfp(cfp);
        // ask monitoring and return to server
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
        throws FailureException {
        return super.handleAcceptProposal(cfp, propose, accept);
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        super.handleRejectProposal(cfp, propose, reject);
    }
}
