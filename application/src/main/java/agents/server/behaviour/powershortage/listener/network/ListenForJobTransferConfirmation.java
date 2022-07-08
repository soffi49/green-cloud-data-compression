package agents.server.behaviour.powershortage.listener.network;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.server.ServerAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Behaviour is responsible for retrieving the information send by the Cloud Network stating that the job transfer was successful
 */
public class ListenForJobTransferConfirmation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobTransferConfirmation.class);
    private final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL));

    private final ServerAgent myServerAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myServerAgent agent which is executing the behaviour
     */
    public ListenForJobTransferConfirmation(final ServerAgent myServerAgent) {
        this.myServerAgent = myServerAgent;
        this.guid = myServerAgent.getName();
    }

    /**
     * Method which listens for the information from cloud network that the transfer for the given job is established.
     * It then verifies in which state is the job and based on that, handles the transfer information.
     */
    @Override
    public void action() {
        final ACLMessage message = myServerAgent.receive(messageTemplate);
        if (nonNull(message)) {
            final PowerShortageJob powerShortageJob = readMessage(message);
            if (Objects.nonNull(powerShortageJob) && nonNull(myServerAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                final String transferredJobId = powerShortageJob.getJobInstanceId().getJobId();
                logger.info("[{}] Transfer of job with id {} was established successfully", guid, transferredJobId);
                final Job jobOnBackUp = myServerAgent.manage().getJobByIdAndStartDate(transferredJobId, powerShortageJob.getJobInstanceId().getStartTime());
                final Job jobOnGreen = myServerAgent.manage().getJobByIdAndEndDate(transferredJobId, powerShortageJob.getJobInstanceId().getStartTime());
                final String jobId = jobOnBackUp.getJobId();
                final boolean willJobFinishBeforeTransfer = jobOnBackUp.getEndTime().isBefore(powerShortageJob.getPowerShortageStart()) ||
                        jobOnBackUp.getEndTime().isEqual(powerShortageJob.getPowerShortageStart());

                if (willJobFinishBeforeTransfer) {
                    logger.info("[{}] Job with id {} will finish before the transfer. Sending cancel transfer information ", guid, jobId);
                    displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
                    myServerAgent.send(prepareTransferCancellationRequest(powerShortageJob, myServerAgent.getOwnerCloudNetworkAgent()));
                } else {
                    logger.info("[{}] Finishing the job with id {} and informing the green source", guid, jobId);
                    final List<AID> receivers = List.of(myServerAgent.getGreenSourceForJobMap().get(jobId));
                    if (Objects.nonNull(jobOnGreen)) {
                        informGreenSourceAboutJobFinish(jobOnGreen, receivers);
                        myServerAgent.getServerJobs().remove(jobOnGreen);
                        myServerAgent.manage().incrementFinishedJobs(jobOnGreen.getJobId());
                    }
                    informGreenSourceAboutJobFinish(jobOnBackUp, receivers);
                    myServerAgent.getServerJobs().remove(jobOnBackUp);
                    myServerAgent.getGreenSourceForJobMap().remove(jobId);
                    myServerAgent.manage().incrementFinishedJobs(jobOnBackUp.getJobId());
                }
            }
        } else {
            block();
        }
    }

    private void informGreenSourceAboutJobFinish(final Job job, final List<AID> receivers) {
        final ACLMessage finishJobMessage = prepareFinishMessage(job.getJobId(), job.getStartTime(), receivers);
        displayMessageArrow(myServerAgent, receivers);
        myServerAgent.send(finishJobMessage);
    }

    private PowerShortageJob readMessage(final ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), PowerShortageJob.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
