package agents.cloudnetwork.behaviour;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.Vector;

/**
 *  Behaviour which is responsible for broadcasting client's job to servers and choosing server to execute the job
 *  (instead of HandleServerCallForProposalResponse)
 */
public class AnnounceNewJobRequest extends ContractNetInitiator {

    public AnnounceNewJobRequest(Agent a, ACLMessage clientsJob) {
        super(a, clientsJob);
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        super.handleAllResponses(responses, acceptances);
    }
}
