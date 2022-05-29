package agents.server.behaviour;

import static common.constant.DFServiceConstants.GS_SERVICE_TYPE;
import static jade.lang.acl.ACLMessage.CFP;
import static mapper.JsonMapper.getMapper;
import static yellowpages.YellowPagesService.search;

import common.message.SendJobMessage;
import agents.server.ServerAgent;
import agents.server.message.RefuseProposalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleCNAJobCallForProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleCNAJobCallForProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(CFP);
    private ServerAgent serverAgent;

    private HandleCNAJobCallForProposal(final ServerAgent serverAgent) {
        super(serverAgent);
    }

    public static HandleCNAJobCallForProposal createFor(final ServerAgent serverAgent) {
        return new HandleCNAJobCallForProposal(serverAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        serverAgent = (ServerAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            logger.info("[{}] Job call for proposal received from {}", myAgent, message.getSender().getLocalName());

            try {
                final Job receivedJob = getMapper().readValue(message.getContent(), Job.class);

                if (receivedJob.getPower() + serverAgent.getPowerInUse() <= serverAgent.getAvailableCapacity()) {
                    logger.info("[{}] Querying Green Source Agents for job proposal", myAgent);
                    var availableGreenSourceAgents = search(serverAgent, GS_SERVICE_TYPE);
                    var callForPower = SendJobMessage
                        .create(receivedJob, availableGreenSourceAgents, CFP)
                        .getMessage();
                    myAgent.send(callForPower);
                } else {
                    logger.info("[{}] Rejecting job proposal", myAgent);
                    myAgent.send(RefuseProposalMessage.create(serverAgent, receivedJob.getClientIdentifier())
                        .getMessage());
                }
            } catch (final JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}