package agents.server.behaviour.powershortage.listener.network;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateServerState;
import static common.constant.MessageProtocolConstants.*;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.server.ServerAgent;
import agents.server.behaviour.FinishJobExecution;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.*;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Behaviour is responsible for retrieving the information send by the Cloud Network stating that the job transfer was successful
 */
public class ListenForJobTransferConfirmation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobTransferConfirmation.class);
    private final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_JOB_TRANSFER_PROTOCOL));

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
            final JobTransfer jobTransfer = readMessage(message);
            if (Objects.nonNull(jobTransfer) && nonNull(myServerAgent.getJobByIdAndStartDate(jobTransfer.getJobId(), jobTransfer.getTransferTime()))) {
                logger.info("[{}] Transfer of job with id {} was established successfully", guid, jobTransfer.getJobId());
                final Job jobOnBackUp = myServerAgent.getJobByIdAndStartDate(jobTransfer.getJobId(), jobTransfer.getTransferTime());
                final Job jobInProgress = myServerAgent.getJobByIdAndEndDate(jobTransfer.getJobId(), jobTransfer.getTransferTime());
                final String jobId = jobOnBackUp.getJobId();
                final boolean willJobFinishBeforeTransfer = jobOnBackUp.getEndTime().isBefore(jobTransfer.getTransferTime()) ||
                        jobOnBackUp.getEndTime().isEqual(jobTransfer.getTransferTime());

                if(willJobFinishBeforeTransfer) {
                    logger.info("[{}] Job with id {} will finish before the transfer. Sending cancel transfer information ", guid, jobId);
                    displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
                    myServerAgent.send(prepareTransferCancellationRequest(jobTransfer, myServerAgent.getOwnerCloudNetworkAgent()));
                } else {
                    logger.info("[{}] Finishing the job and informing the green source {}", guid, jobId);
                    final List<AID> receivers = List.of(myServerAgent.getGreenSourceForJobMap().get(jobId));
                    if(Objects.nonNull(jobInProgress)) {
                        informGreenSourceAboutJobFinish(jobInProgress, receivers);
                        myServerAgent.getServerJobs().remove(jobInProgress);
                    }
                    informGreenSourceAboutJobFinish(jobOnBackUp, receivers);
                    myServerAgent.getServerJobs().remove(jobOnBackUp);
                    myServerAgent.getGreenSourceForJobMap().remove(jobId);
                    updateServerState(myServerAgent);
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

    private JobTransfer readMessage(final ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), JobTransfer.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
