package agents.server.behaviour.powershortage.announcer;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.*;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageInformation;

import agents.server.ServerAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.constant.InvalidJobIdConstant;
import domain.GreenSourceData;
import domain.job.PowerShortageTransfer;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import messages.domain.ReplyMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

/**
 * Behaviours responsible for sending the transfer call for proposal to remaining green sources and choosing the one which
 * will handle the job when the power shortage happens
 */
public class AnnouncePowerRequestTransfer extends ContractNetInitiator {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncePowerRequestTransfer.class);

    private final ServerAgent myServerAgent;
    private final PowerShortageTransfer powerShortageTransfer;

    /**
     * Behaviour constructor
     *
     * @param agent                 agent which executes the behaviour
     * @param powerRequest          call for proposal containing the details regarding power needed to execute the job
     * @param powerShortageTransfer power shortage transfer for which the call for proposal is executed
     */
    public AnnouncePowerRequestTransfer(final Agent agent, final ACLMessage powerRequest, final PowerShortageTransfer powerShortageTransfer) {
        super(agent, powerRequest);
        this.myServerAgent = (ServerAgent) myAgent;
        this.powerShortageTransfer = powerShortageTransfer;
    }

    /**
     * Method which waits for all Green Source Agent responses. It is responsible for analyzing the received proposals,
     * choosing the Green Source Agent for power job transfer execution and rejecting the remaining Green Source Agents.
     * If no green source is available, it passes the information about the need of the job transfer to the parent Cloud Network
     *
     * @param responses   retrieved responses from Green Source Agents
     * @param acceptances vector containing accept proposal message sent back to the chosen green source (not used)
     */
    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        final List<ACLMessage> proposals = retrieveProposals(responses);

        if (responses.isEmpty()) {
            logger.info("[{}] No responses were retrieved", myAgent.getName());
        } else if (proposals.isEmpty()) {
            logger.info("[{}] No green sources are available for the power transfer. Passing the information to the cloud network", myAgent.getName());
            displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
            myServerAgent.send(preparePowerShortageInformation(powerShortageTransfer, myServerAgent.getOwnerCloudNetworkAgent()));
        } else {
            final List<ACLMessage> validProposals = retrieveValidMessages(proposals, GreenSourceData.class);
            if (!validProposals.isEmpty()) {
                final ACLMessage chosenGreenSourceOffer = myServerAgent.chooseGreenSourceToExecuteJob(validProposals);
                final GreenSourceData chosenGreenSourceData = readMessageContent(chosenGreenSourceOffer);
                final String jobId = chosenGreenSourceData.getJobId();
                logger.info("[{}] Chosen Green Source for the job {} transfer: {}", myAgent.getName(), jobId, chosenGreenSourceOffer.getSender().getLocalName());

                displayMessageArrow(myServerAgent, myServerAgent.getGreenSourceForJobMap().get(jobId));
                displayMessageArrow(myServerAgent, chosenGreenSourceOffer.getAllReceiver());

                myAgent.send(ReplyMessageFactory.prepareAcceptReplyWithProtocol(chosenGreenSourceOffer.createReply(), jobId, powerShortageTransfer.getStartTime(), POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL));
                rejectJobOffers(myServerAgent, jobId, chosenGreenSourceOffer, proposals);
            } else {
                handleInvalidProposals(proposals);
            }
        }
    }

    private void handleInvalidProposals(final List<ACLMessage> proposals) {
        logger.info("I didn't understand any proposal from Green Energy Agents");
        rejectJobOffers(myServerAgent, InvalidJobIdConstant.INVALID_JOB_ID, null, proposals);
    }

    private GreenSourceData readMessageContent(final ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), GreenSourceData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
