package agents.cloudnetwork.behaviour;

import static agents.cloudnetwork.CloudNetworkAgentConstants.SERVER_AGENTS;
import static common.constant.MessageProtocolConstants.CLIENT_JOB_CFP_PROTOCOL;
import static common.constant.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import messages.domain.SendJobCallForProposalMessage;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Behaviour which is responsible for handling upcoming call for proposals from clients
 */
public class ReceiveJobRequests extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveJobRequests.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(CFP), MatchProtocol(CLIENT_JOB_CFP_PROTOCOL));

    /**
     * Method listens for the upcoming job call for proposals from the Client Agents. It announces the job call for proposal
     * to the network by sending call for proposal with job characteristics to owned Server Agents.
     */
    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                logger.debug("[{}] Sending call for proposal to Server Agents", myAgent.getName());

                final Job job = getMapper().readValue(message.getContent(), Job.class);
                final List<AID> serverAgents = (List<AID>) getParent().getDataStore().get(SERVER_AGENTS);
                final ACLMessage cfp = SendJobCallForProposalMessage.create(job, serverAgents, CNA_JOB_CFP_PROTOCOL).getMessage();

                myAgent.addBehaviour(new AnnounceNewJobRequest(myAgent, cfp, message.createReply()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
