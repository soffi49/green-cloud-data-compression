package agents.client.behaviour;

import agents.client.ClientAgent;
import domain.Job;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import java.util.List;
import message.client.SendJobMessage;

import static jade.lang.acl.ACLMessage.PROPOSE;

/**
 * One shot behaviour for client agent. Its purpose is to
 * announce job over the Cloud Network Agents
 *
 */
public class SendJobProposal extends OneShotBehaviour {

    private final ClientAgent clientAgent;
    private final Job job;

    private SendJobProposal(ClientAgent clientAgent, Job job) {
        this.clientAgent = clientAgent;
        this.job = job;
    }

    public static SendJobProposal createFor(ClientAgent clientAgent, Job job) {
        return new SendJobProposal(clientAgent, job);
    }

    @Override
    public void action() {
        final List<AID> agentsCNA = clientAgent.initializeCloudNetworkAgentList(myAgent);
        clientAgent.setMessagesSentCount(agentsCNA.size());
        System.out.println("[Client] Sending job proposal to CNA");
        myAgent.send(SendJobMessage.create(job,agentsCNA, PROPOSE).getMessage());
    }
}
