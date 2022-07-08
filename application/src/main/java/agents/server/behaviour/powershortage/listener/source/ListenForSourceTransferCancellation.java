package agents.server.behaviour.powershortage.listener.source;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.server.ServerAgent;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour is responsible for listening for the power job transfer cancellation message coming from the green source agent
 */
public class ListenForSourceTransferCancellation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForSourceTransferCancellation.class);

    private final MessageTemplate messageTemplate;
    private final ServerAgent myServerAgent;
    private final AID chosenGreenSourceForTransfer;

    /**
     * Behaviour constructor
     *
     * @param myAgent                      agent executing the behaviour
     * @param chosenGreenSourceForTransfer green source which was supposed to execute the transferred job
     * @param affectedGreenSource          green source affected by the power fluctuation
     */
    public ListenForSourceTransferCancellation(final Agent myAgent, final AID chosenGreenSourceForTransfer, final AID affectedGreenSource) {
        super(myAgent);
        this.myServerAgent = (ServerAgent) myAgent;
        this.chosenGreenSourceForTransfer = chosenGreenSourceForTransfer;
        this.messageTemplate = and(MatchPerformative(REQUEST), and(MatchProtocol(CANCELLED_TRANSFER_PROTOCOL), MatchSender(affectedGreenSource)));

    }

    /**
     * Method listens for the message coming from Green Source requesting the transfer cancellation
     * It sends the cancellation request to the green source chosen for the job transfer
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);
        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
                logger.info("[{}] Sending the request for transfer cancellation to {}", myAgent.getName(), chosenGreenSourceForTransfer.getLocalName());
                displayMessageArrow(myServerAgent, chosenGreenSourceForTransfer);
                myServerAgent.send(prepareTransferCancellationRequest(powerShortageJob, chosenGreenSourceForTransfer));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
