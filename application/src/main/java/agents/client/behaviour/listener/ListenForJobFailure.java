package agents.client.behaviour.listener;

import agents.client.ClientAgent;
import com.gui.agents.ClientAgentNode;
import com.gui.agents.domain.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static common.constant.MessageProtocolConstants.*;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;

/**
 * Behaviour which handles the failure of the execution of the job
 */
public class ListenForJobFailure extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobFailure.class);
    private static final MessageTemplate messageTemplate = and(
                    MatchPerformative(FAILURE),
                    MatchProtocol(FAILED_JOB_PROTOCOL));

    private final ClientAgent myClientAgent;

    /**
     * Behaviours constructor.
     *
     * @param clientAgent agent executing the behaviour
     */
    public ListenForJobFailure(final ClientAgent clientAgent) {
        super(clientAgent);
        this.myClientAgent = clientAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);
        if (Objects.nonNull(message)) {
            logger.info("[{}] The execution of my job has failed", myClientAgent.getName());
            myClientAgent.getGuiController().updateClientsCountByValue(-1);
            myClientAgent.doDelete();
        } else {
            block();
        }
    }
}
