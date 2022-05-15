package agents.client.behaviour;

import static common.CommonUtils.getAgentsFromDF;
import static jade.lang.acl.ACLMessage.CFP;

import agents.client.ClientAgent;
import agents.client.message.SendJobMessage;
import common.GroupConstants;
import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import java.util.List;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One shot behaviour for client agent. Its purpose is to announce job over the Cloud Network Agents
 */
public class SendJobCallForProposal extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(SendJobCallForProposal.class);

    private final Job job;

    private SendJobCallForProposal(final ClientAgent clientAgent, final Job job) {
        super(clientAgent);
        this.job = job;
    }

    public static SendJobCallForProposal createFor(final ClientAgent clientAgent, final Job job) {
        return new SendJobCallForProposal(clientAgent, job);
    }

    @Override
    public void action() {
        final List<AID> agentsCNA = findCloudNetworkAgents(myAgent);
        ((ClientAgent) myAgent).setMessagesSentCount(agentsCNA.size());
        if(agentsCNA.isEmpty()) {
            logger.info("[{}] No Cloud Network Agents were found", myAgent);
            myAgent.doDelete();
        }
        logger.info("[{}] Sending job call for proposal to Cloud Network Agents", myAgent);
        myAgent.send(SendJobMessage.create(job, agentsCNA, CFP).getMessage());
    }

    public List<AID> findCloudNetworkAgents(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }
}
