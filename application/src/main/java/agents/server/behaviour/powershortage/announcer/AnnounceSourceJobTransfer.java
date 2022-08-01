package agents.server.behaviour.powershortage.announcer;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL;
import static domain.job.JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static domain.job.JobStatusEnum.ON_HOLD;
import static domain.job.JobStatusEnum.ON_HOLD_SOURCE_SHORTAGE;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.MessagingUtils.retrieveValidMessages;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageTransferRequest;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.listener.ListenForSourceJobTransferConfirmation;
import agents.server.behaviour.powershortage.transfer.RequestJobTransferInCloudNetwork;
import common.mapper.JobMapper;
import domain.GreenSourceData;
import domain.job.JobInstanceIdentifier;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import messages.domain.ReplyMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 * Behaviours responsible for sending the transfer call for proposal to remaining green sources and choosing the one which
 * will handle the job when the power shortage happens
 */
public class AnnounceSourceJobTransfer extends ContractNetInitiator {

    private static final Logger logger = LoggerFactory.getLogger(AnnounceSourceJobTransfer.class);

    private final ServerAgent myServerAgent;
    private final PowerJob jobToTransfer;
    private final JobInstanceIdentifier jobTransferInstance;
    private final OffsetDateTime powerShortageStart;
    private final ACLMessage greenSourceRequest;

    /**
     * Behaviour constructor
     *
     * @param agent              agent which executes the behaviour
     * @param powerRequest       call for proposal containing the details regarding power needed to execute the job
     * @param greenSourceRequest green source power transfer request
     * @param jobToTransfer      job to be transferred
     * @param powerShortageStart time when the power shortage starts
     */
    public AnnounceSourceJobTransfer(final Agent agent,
                                     final ACLMessage powerRequest,
                                     final ACLMessage greenSourceRequest,
                                     final PowerJob jobToTransfer,
                                     final OffsetDateTime powerShortageStart) {
        super(agent, powerRequest);
        this.myServerAgent = (ServerAgent) myAgent;
        this.jobToTransfer = jobToTransfer;
        this.greenSourceRequest = greenSourceRequest;
        this.powerShortageStart = powerShortageStart;
        this.jobTransferInstance = JobMapper.mapToJobInstanceId(jobToTransfer);
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
            handleTransferFailure();
        } else if (proposals.isEmpty()) {
            logger.info("[{}] No green sources are available for the power transfer of job {}. Passing the information to the cloud network", myAgent.getName(), jobTransferInstance.getJobId());
            final PowerShortageJob jobTransfer = JobMapper.mapToPowerShortageJob(jobToTransfer, powerShortageStart);
            final ACLMessage transferMessage = preparePowerShortageTransferRequest(jobTransfer, myServerAgent.getOwnerCloudNetworkAgent());
            displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
            myServerAgent.addBehaviour(new RequestJobTransferInCloudNetwork(myServerAgent, transferMessage, greenSourceRequest, jobTransfer, false));
        } else {
            final List<ACLMessage> validProposals = retrieveValidMessages(proposals, GreenSourceData.class);
            if (!validProposals.isEmpty()) {
                final ACLMessage chosenGreenSourceOffer = myServerAgent.chooseGreenSourceToExecuteJob(validProposals);
                final String jobId = jobTransferInstance.getJobId();
                logger.info("[{}] Chosen Green Source for the job {} transfer: {}", myAgent.getName(), jobId, chosenGreenSourceOffer.getSender().getLocalName());

                displayMessageArrow(myServerAgent, myServerAgent.getGreenSourceForJobMap().get(jobId));
                displayMessageArrow(myServerAgent, chosenGreenSourceOffer.getSender());

                myServerAgent.addBehaviour(new ListenForSourceJobTransferConfirmation(myServerAgent, jobTransferInstance, greenSourceRequest));
                myAgent.send(ReplyMessageFactory.prepareAcceptReplyWithProtocol(chosenGreenSourceOffer.createReply(), jobTransferInstance, POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL));
                rejectJobOffers(myServerAgent, jobTransferInstance, chosenGreenSourceOffer, proposals);
            } else {
                handleInvalidProposals(proposals);
            }
        }
    }

    private void handleInvalidProposals(final List<ACLMessage> proposals) {
        logger.info("I didn't understand any proposal from Green Energy Agents");
        handleTransferFailure();
        rejectJobOffers(myServerAgent, jobTransferInstance, null, proposals);
    }

    private void handleTransferFailure() {
        myServerAgent.send(prepareReply(greenSourceRequest.createReply(), jobTransferInstance, ACLMessage.FAILURE));
        if(Objects.nonNull(myServerAgent.manage().getJobByIdAndStartDate(jobTransferInstance))) {
            if (myServerAgent.manage().getBackUpAvailableCapacity(jobToTransfer.getStartTime(), jobToTransfer.getEndTime(), jobTransferInstance) < jobToTransfer.getPower()) {
                logger.info("[{}] There is not enough back up power to support the job {}. Putting job on hold", myAgent.getName(), jobToTransfer.getJobId());
                myServerAgent.getServerJobs().replace(myServerAgent.manage().getJobByIdAndStartDate(jobTransferInstance), ON_HOLD_SOURCE_SHORTAGE);
            } else {
                logger.info("[{}] Putting the job {} on back up power", myAgent.getName(), jobToTransfer.getJobId());
                myServerAgent.getServerJobs().replace(myServerAgent.manage().getJobByIdAndStartDate(jobTransferInstance), IN_PROGRESS_BACKUP_ENERGY);
            }
            myServerAgent.manage().updateServerGUI();
        }
    }
}
