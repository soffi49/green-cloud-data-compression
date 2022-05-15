package agents.cloudnetwork.behaviour;

import agents.cloudnetwork.CloudNetworkAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

public class HandleRejectJobProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleRejectJobProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(REJECT_PROPOSAL);

    private HandleRejectJobProposal(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleRejectJobProposal createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleRejectJobProposal(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            logger.info("[{}] Client {} rejected the job proposal", myAgent, message.getSender().getLocalName());
        } else {
            block();
        }
    }
}
