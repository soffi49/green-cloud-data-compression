package agents.client.behaviour;

import agents.client.ClientAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.INFORM;

/**
 * Behaviour which handles the information that the job execution is done 
 */
public class WaitForJobResult extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(WaitForJobResult.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(INFORM);

    private ClientAgent clientAgent;

    private WaitForJobResult(final ClientAgent clientAgent) {
        super(clientAgent);
    }

    public static WaitForJobResult createFor(final ClientAgent clientAgent) {
        return new WaitForJobResult(clientAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        clientAgent = (ClientAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            if (message.getConversationId().equals("FINISHED")) {
                logger.info("[{}] The execution of my job finished! : )", myAgent);
                myAgent.doDelete();
            } else if (message.getConversationId().equals("STARTED")) {
                logger.info("[{}] The execution of my job started!", myAgent);
            }
        } else {
            block();
        }
    }
}
