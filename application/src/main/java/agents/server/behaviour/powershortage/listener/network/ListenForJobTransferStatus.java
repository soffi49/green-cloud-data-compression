package agents.server.behaviour.powershortage.listener.network;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_TRANSFER_REFUSAL;
import static domain.job.JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.server.ServerAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for retrieving the information send by the Cloud Network with the status if the
 * job transfer was successful / unsuccessful
 */
public class ListenForJobTransferStatus extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobTransferStatus.class);
    private final MessageTemplate messageTemplate = and(MatchPerformative(INFORM),
        or(MatchProtocol(POWER_SHORTAGE_TRANSFER_REFUSAL), MatchProtocol(POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL)));

    private final ServerAgent myServerAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myServerAgent agent which is executing the behaviour
     */
    public ListenForJobTransferStatus(final ServerAgent myServerAgent) {
        this.myServerAgent = myServerAgent;
        this.guid = myServerAgent.getName();
    }

    /**
     * Method which listens for the information from cloud network about the transfer for the given job:
     * - if couldn't been established. Due to that, it updates the states of the job.
     * - if successful it then verifies in which state is the job and based on that, handles the transfer information.
     */
    @Override
    public void action() {
        final ACLMessage message = myServerAgent.receive(messageTemplate);
        if (nonNull(message) && message.getProtocol().equals(POWER_SHORTAGE_TRANSFER_REFUSAL)) {
            final PowerShortageJob powerShortageJob = readMessage(message);
            if (Objects.nonNull(powerShortageJob) && nonNull(myServerAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                final String transferredJobId = powerShortageJob.getJobInstanceId().getJobId();
                logger.info("[{}] Transfer of job with id {} was unsuccessful! Supplying the job with back up power", guid, transferredJobId);
                final Job jobOnBackUp = myServerAgent.manage().getJobByIdAndStartDate(transferredJobId, powerShortageJob.getJobInstanceId().getStartTime());
                final String jobId = jobOnBackUp.getJobId();

                logger.info("[{}] Informing green source to switch the job {} on hold", guid, jobId);
                final AID receiver = myServerAgent.getGreenSourceForJobMap().get(jobId);
                myServerAgent.getServerJobs().replace(jobOnBackUp, IN_PROGRESS_BACKUP_ENERGY);
                myServerAgent.manage().updateServerGUI();
                displayMessageArrow(myServerAgent, receiver);
                myServerAgent.send(prepareJobPowerShortageInformation(powerShortageJob,receiver, POWER_SHORTAGE_TRANSFER_REFUSAL));
            }
        } else if (nonNull(message) && message.getProtocol().equals(POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL)) {
            final PowerShortageJob powerShortageJob = readMessage(message);
            if (Objects.nonNull(powerShortageJob) && nonNull(myServerAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                final String transferredJobId = powerShortageJob.getJobInstanceId().getJobId();
                logger.info("[{}] Transfer of job with id {} was established successfully", guid, transferredJobId);
                final Job jobOnBackUp = myServerAgent.manage().getJobByIdAndStartDate(transferredJobId, powerShortageJob.getJobInstanceId().getStartTime());
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
                    final boolean isJobUnique = myServerAgent.manage().isJobUnique(jobId);
                    informGreenSourceAboutJobFinish(jobOnBackUp, receivers);
                    myServerAgent.getServerJobs().remove(jobOnBackUp);
                    if(jobOnBackUp.getStartTime().isBefore(getCurrentTime())) {
                        myServerAgent.manage().incrementFinishedJobs(jobOnBackUp.getJobId());
                    }
                    if (isJobUnique) {
                        myServerAgent.getGreenSourceForJobMap().remove(jobId);
                        myServerAgent.manage().updateServerGUI();
                    }
                }
            }
        } else {
            block();
        }
    }

    private PowerShortageJob readMessage(final ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), PowerShortageJob.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private void informGreenSourceAboutJobFinish(final Job job, final List<AID> receivers) {
        final ACLMessage finishJobMessage = prepareFinishMessage(job.getJobId(), job.getStartTime(), receivers);
        displayMessageArrow(myServerAgent, receivers);
        myServerAgent.send(finishJobMessage);
    }
}
