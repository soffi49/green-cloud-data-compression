package agents.server.behaviour.powershortage.transfer;

import static agents.server.domain.ServerAgentConstants.JOB_TRANSFER_RETRY_TIMEOUT;
import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL;
import static domain.job.JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static domain.job.JobStatusEnum.ON_HOLD;
import static domain.job.JobStatusEnum.ON_HOLD_SOURCE_SHORTAGE;
import static java.util.Objects.nonNull;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Behaviour is responsible for requesting the transfer of the job which is affected by the green source
 * power shortage
 */
public class RequestJobTransferInCloudNetwork extends AchieveREInitiator {

    private static final Logger logger = LoggerFactory.getLogger(RequestJobTransferInCloudNetwork.class);

    private final ServerAgent myServerAgent;
    private final PowerShortageJob jobToTransfer;
    private final ACLMessage greenSourceRequest;
    private final ACLMessage transferMessage;
    private final boolean isServerTransfer;
    private final String guid;

    /**
     * Behaviours constructor
     *
     * @param agent            server executing the behaviour
     * @param transferMessage  message with the transfer request
     * @param jobToTransfer    job which is to be transferred
     * @param isServerTransfer flag indicating if the transfer was initiated by server or green source
     */
    public RequestJobTransferInCloudNetwork(ServerAgent agent,
                                            ACLMessage transferMessage,
                                            ACLMessage greenSourceRequest,
                                            PowerShortageJob jobToTransfer,
                                            boolean isServerTransfer) {
        super(agent, transferMessage);
        this.myServerAgent = agent;
        this.guid = myServerAgent.getLocalName();
        this.jobToTransfer = jobToTransfer;
        this.greenSourceRequest = greenSourceRequest;
        this.isServerTransfer = isServerTransfer;
        this.transferMessage = transferMessage;
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        logger.info("[{}] Cloud Network {} is working on the job {} transfer",
                    guid, myServerAgent.getOwnerCloudNetworkAgent().getLocalName(), jobToTransfer.getJobInstanceId().getJobId());
    }

    @Override
    protected void handleInform(ACLMessage agree) {
        if (Objects.nonNull(myServerAgent.manage().getJobByIdAndStartDate(jobToTransfer.getJobInstanceId()))) {
            final String jobId = jobToTransfer.getJobInstanceId().getJobId();
            logger.info("[{}] Transfer of job with id {} was established successfully", guid, jobId);
            final Job jobOnBackUp = myServerAgent.manage().getJobByIdAndStartDate(jobToTransfer.getJobInstanceId());
            final boolean willJobFinishBeforeTransfer = jobOnBackUp.getEndTime().isBefore(jobToTransfer.getPowerShortageStart()) ||
                    jobOnBackUp.getEndTime().isEqual(jobToTransfer.getPowerShortageStart());

            if (willJobFinishBeforeTransfer) {
                logger.info("[{}] Job with id {} will finish before the transfer. Sending cancel transfer information ", guid, jobId);
                displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
                myServerAgent.send(prepareJobPowerShortageInformation(jobToTransfer, myServerAgent.getOwnerCloudNetworkAgent(), CANCELLED_TRANSFER_PROTOCOL));
                if (!isServerTransfer) {
                    myServerAgent.send(prepareReply(greenSourceRequest.createReply(), jobToTransfer.getJobInstanceId(), ACLMessage.FAILURE));
                }
            } else {
                logger.info("[{}] Finishing the job with id {} and informing the green source", guid, jobId);
                final boolean isUnique = myServerAgent.manage().isJobUnique(jobId);
                informGreenSourceAboutJobFinish(jobOnBackUp);
                myServerAgent.getServerJobs().remove(jobOnBackUp);
                if (jobOnBackUp.getStartTime().isBefore(getCurrentTime())) {
                    myServerAgent.manage().incrementFinishedJobs(jobOnBackUp.getJobId());
                }
                if (isUnique) {
                    myServerAgent.getGreenSourceForJobMap().remove(jobId);
                    myServerAgent.manage().updateServerGUI();
                }
            }
        }
    }

    @Override
    protected void handleFailure(ACLMessage failure) {
        if (nonNull(myServerAgent.manage().getJobByIdAndStartDate(jobToTransfer.getJobInstanceId()))) {
            final String jobId = jobToTransfer.getJobInstanceId().getJobId();
            logger.info("[{}] Transfer of job with id {} was unsuccessful!", guid, jobId);
            final Job jobOnBackUp = myServerAgent.manage().getJobByIdAndStartDate(jobToTransfer.getJobInstanceId());
            if(isServerTransfer) {
                logger.info("[{}] Putting job {} on hold", myAgent.getName(), jobOnBackUp.getJobId());
                myServerAgent.getServerJobs().replace(jobOnBackUp, ON_HOLD);
                final Behaviour requestBehaviour = new RequestJobTransferInCloudNetwork(myServerAgent, transferMessage, greenSourceRequest, jobToTransfer, true);
                myServerAgent.addBehaviour(new RetryJobTransferRequestInCloudNetwork(myServerAgent, JOB_TRANSFER_RETRY_TIMEOUT, jobToTransfer, requestBehaviour));
                myServerAgent.removeBehaviour(this);
            } else if (myServerAgent.manage().getBackUpAvailableCapacity(jobOnBackUp.getStartTime(), jobOnBackUp.getEndTime(), jobToTransfer.getJobInstanceId()) <= jobOnBackUp.getPower() ) {
                logger.info("[{}] There is not enough back up power to support the job {}. Putting job on hold", myAgent.getName(), jobOnBackUp.getJobId());
                myServerAgent.getServerJobs().replace(jobOnBackUp, ON_HOLD_SOURCE_SHORTAGE);
            } else {
                logger.info("[{}] Putting the job {} on back up power", myAgent.getName(), jobOnBackUp.getJobId());
                myServerAgent.getServerJobs().replace(jobOnBackUp, IN_PROGRESS_BACKUP_ENERGY);
            }
            myServerAgent.manage().updateServerGUI();
            logger.info("[{}] Informing green source to switch the job {} on hold", guid, jobId);
            if (isServerTransfer) {
                final AID receiver = myServerAgent.getGreenSourceForJobMap().get(jobId);
                displayMessageArrow(myServerAgent, receiver);
                myServerAgent.send(prepareJobPowerShortageInformation(jobToTransfer, receiver, SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL));
            } else {
                displayMessageArrow(myServerAgent, greenSourceRequest.getSender());
                myServerAgent.send(prepareReply(greenSourceRequest.createReply(), jobToTransfer.getJobInstanceId(), ACLMessage.FAILURE));
            }
        }
    }

    private void informGreenSourceAboutJobFinish(final Job job) {
        if (isServerTransfer) {
            final List<AID> receivers = List.of(myServerAgent.getGreenSourceForJobMap().get(job.getJobId()));
            final ACLMessage finishJobMessage = prepareFinishMessage(job.getJobId(), job.getStartTime(), receivers);
            displayMessageArrow(myServerAgent, receivers);
            myServerAgent.send(finishJobMessage);
        } else {
            displayMessageArrow(myServerAgent, greenSourceRequest.getSender());
            myServerAgent.send(prepareReply(greenSourceRequest.createReply(), jobToTransfer.getJobInstanceId(), ACLMessage.INFORM));
        }
    }
}
