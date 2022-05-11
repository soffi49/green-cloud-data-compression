package agents.server.behaviour;

import static common.CommonUtils.sendJobProposalToAgents;
import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import agents.server.message.ProposalResponseMessage;
import agents.server.message.RefuseProposalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.GreenSourceData;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAgentReadMessages extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ServerAgentReadMessages.class);

    private final ServerAgent serverAgent;

    public ServerAgentReadMessages(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = serverAgent.receive();
        if (Objects.nonNull(message)) {
            switch (message.getPerformative()) {
                case PROPOSE:
                    try {
                        handleProposeMessageType(message);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case REJECT_PROPOSAL, ACCEPT_PROPOSAL:
                    handleProposalResponse(message);
                    break;
            }
        } else {
            block();
        }
    }

    private void handleProposeMessageType(ACLMessage message) throws JsonProcessingException {
        final Job receivedJob = getMapper().readValue(message.getContent(), Job.class);
        logger.info("{} Proposal received", myAgent);
        if (receivedJob.getPower() + serverAgent.getPowerInUse() <=  serverAgent.getAvailableCapacity()) {
            sendJobProposalToAgents(myAgent, serverAgent.getGreenSourceAgentsList(), receivedJob);
            logger.info("{} Proposal sent to GS", myAgent);
            serverAgent.setResponsesReceivedCount(0);
        } else {
            logger.info("{} Proposal rejected", myAgent);
            serverAgent.send(RefuseProposalMessage.create(serverAgent).getMessage());
        }
    }

    private void handleProposalResponse(ACLMessage message) {
        if (serverAgent.getResponsesReceivedCount() < serverAgent.getMessagesSentCount()) {
            try {
                logger.info("{} Proposal send to gs", myAgent);
                serverAgent.getAcceptingGreenSources()
                    .put(message.getSender(), (GreenSourceData) message.getContentObject());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        } else {
            if (serverAgent.getAcceptingGreenSources().isEmpty()) {
                serverAgent.send(ProposalResponseMessage.create(serverAgent, REJECT_PROPOSAL).getMessage());
                logger.info("{} Handle gs response: proposal from CNA - Rejected", myAgent);
            }
            else {
                serverAgent.send(ProposalResponseMessage.create(serverAgent, ACCEPT_PROPOSAL).getMessage());
                logger.info("{} Handle gs response: proposal from CNA - Accepted", myAgent);
            }
        }
    }
}
