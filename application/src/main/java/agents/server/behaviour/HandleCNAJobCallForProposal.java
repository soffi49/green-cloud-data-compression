package agents.server.behaviour;

import agents.client.message.SendJobMessage;
import agents.cloudnetwork.behaviour.HandleClientJobCallForProposal;
import agents.server.ServerAgent;
import agents.server.message.RefuseProposalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.ACLMessage.QUERY_IF;
import static mapper.JsonMapper.getMapper;

public class HandleCNAJobCallForProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleCNAJobCallForProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(CFP);

    private HandleCNAJobCallForProposal(final ServerAgent serverAgent) {
        super(serverAgent);
    }

    public static HandleCNAJobCallForProposal createFor(final ServerAgent serverAgent) {
        return new HandleCNAJobCallForProposal(serverAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            logger.info("[{}] Job call for proposal received from {}", myAgent, message.getSender().getLocalName());

            try {
                final Job receivedJob = getMapper().readValue(message.getContent(), Job.class);

                if (receivedJob.getPower() + ((ServerAgent) myAgent).getPowerInUse() <= ((ServerAgent) myAgent).getAvailableCapacity()) {
                    logger.info("[{}] Querying Green Source Agents for job proposal", myAgent);
                    myAgent.send(SendJobMessage.create(receivedJob, ((ServerAgent) myAgent).getGreenSourceAgentsList(), CFP).getMessage());
                } else {
                    logger.info("[{}] Rejecting job proposal", myAgent);
                    myAgent.send(RefuseProposalMessage.create((ServerAgent) myAgent).getMessage());
                }
            } catch (final JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
