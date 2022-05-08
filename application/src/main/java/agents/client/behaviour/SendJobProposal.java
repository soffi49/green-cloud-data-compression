package agents.client.behaviour;

import static jade.lang.acl.ACLMessage.PROPOSE;

import agents.client.ClientAgent;
import agents.client.message.SendJobMessage;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One shot behaviour for client agent. Its purpose is to announce job over the Cloud Network Agents
 */
public class SendJobProposal extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(SendJobProposal.class);

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
        logger.info("{} Sending job proposal to CNA", myAgent);
        myAgent.send(SendJobMessage.create(job, agentsCNA, PROPOSE).getMessage());
    }
}
