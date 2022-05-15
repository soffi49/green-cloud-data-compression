package agents.cloudnetwork.behaviour;

import agents.client.behaviour.HandleCallForProposalResponse;
import agents.cloudnetwork.CloudNetworkAgent;
import domain.ImmutableCloudNetworkData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static mapper.JsonMapper.getMapper;

public class HandleJobCallForProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleJobCallForProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(CFP);

    private HandleJobCallForProposal(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleJobCallForProposal createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleJobCallForProposal(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            logger.info("[{}] Job call for proposal received from {}", myAgent, message.getSender().getLocalName());

            final ACLMessage response = message.createReply();
            response.setPerformative(PROPOSE);                                  //currently the CNA agents can only accept CFP
            try {
                final ImmutableCloudNetworkData data = ImmutableCloudNetworkData.builder()
                        .inUsePower(((CloudNetworkAgent) myAgent).getInUsePower())
                        .jobsCount(((CloudNetworkAgent) myAgent).getJobsCount())
                        .build();
                response.setContent(getMapper().writeValueAsString(data));
            } catch (final IOException e) {
                e.printStackTrace();
            }

            logger.info("[{}] Sending proposal to {}", myAgent, message.getSender().getLocalName());
            myAgent.send(response);

        } else {
            block();
        }
    }
}
