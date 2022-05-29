package agents.client.behaviour;

import agents.client.ClientAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;

/**
 * Behaviour which handles the information that the job execution is done
 */
public class WaitForJobResult extends CyclicBehaviour {
    private static final Logger logger = LoggerFactory.getLogger(WaitForJobResult.class);
    private static final MessageTemplate messageTemplate = and(MatchProtocol(FINISH_JOB_PROTOCOL), MatchPerformative(INFORM));

    public WaitForJobResult(final ClientAgent clientAgent) {
        super(clientAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);
        if (Objects.nonNull(message)) {
            logger.info("[{}] The execution of my job finished! :)", myAgent);
            myAgent.doDelete();
        } else {
            block();
        }
    }
}
