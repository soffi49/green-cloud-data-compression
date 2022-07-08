package agents.cloudnetwork.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour listens for messages coming from the affected server which decided to cancel the job transfer
 */
public class ListenForServerTransferCancellation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForServerTransferCancellation.class);

    private final MessageTemplate messageTemplate;
    private final CloudNetworkAgent myCloudNetworkAgent;
    private final AID chosenServerForTransfer;

    /**
     * Behaviour constructor
     *
     * @param myAgent                 agent executing the behaviour
     * @param chosenServerForTransfer server which was supposed to execute the transferred job
     * @param affectedServer          server affected by the power fluctuation
     */
    public ListenForServerTransferCancellation(final Agent myAgent, final AID chosenServerForTransfer, final AID affectedServer) {
        super(myAgent);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.chosenServerForTransfer = chosenServerForTransfer;
        this.messageTemplate = and(MatchPerformative(REQUEST), and(MatchProtocol(CANCELLED_TRANSFER_PROTOCOL), MatchSender(affectedServer)));

    }

    /**
     * Method listens for the message coming from the Server requesting the transfer cancellation
     * It sends the cancellation request to the server chosen for the job transfer
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
                logger.info("[{}] Sending the request for transfer cancellation to {}", myAgent.getName(), chosenServerForTransfer.getLocalName());
                displayMessageArrow(myCloudNetworkAgent, chosenServerForTransfer);
                myCloudNetworkAgent.send(prepareTransferCancellationRequest(powerShortageJob, chosenServerForTransfer));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}