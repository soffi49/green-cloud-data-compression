package agents.greenenergy.behaviour.powershortage.transfer;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;

import agents.greenenergy.GreenEnergyAgent;
import common.mapper.JobMapper;
import domain.job.ImmutablePowerShortageJob;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

/**
 * Behaviour is responsible for handling the transfer of power jobs affected by the power shortage
 */
public class RequestPowerJobTransfer extends AchieveREInitiator {

    private static final Logger logger = LoggerFactory.getLogger(RequestPowerJobTransfer.class);

    private final GreenEnergyAgent myGreenAgent;
    private final PowerJob jobToTransfer;
    private final OffsetDateTime powerShortageStart;
    private final String guid;

    /**
     * Behaviours constructor
     *
     * @param agent              green source executing the behaviour
     * @param transferRequest    message with request for the power jobs transfer
     * @param jobToTransfer      job that is requested to be transferred
     * @param powerShortageStart time when the power shortage starts
     */
    public RequestPowerJobTransfer(GreenEnergyAgent agent,
                                   ACLMessage transferRequest,
                                   PowerJob jobToTransfer,
                                   OffsetDateTime powerShortageStart) {
        super(agent, transferRequest);
        this.myGreenAgent = agent;
        this.jobToTransfer = jobToTransfer;
        this.guid = myGreenAgent.getLocalName();
        this.powerShortageStart = powerShortageStart;
    }

    @Override
    protected void handleAgree(ACLMessage agree) {
        logger.info("[{}] Server {} is working on the job {} transfer",
                    guid, myGreenAgent.getOwnerServer().getLocalName(), jobToTransfer.getJobId());
    }

    @Override
    protected void handleInform(ACLMessage agree) {
        if (myGreenAgent.getPowerJobs().containsKey(jobToTransfer)) {
            final String jobId = jobToTransfer.getJobId();
            logger.info("[{}] Transfer of job with id {} was established successfully", guid, jobId);
            final boolean willJobFinishBeforeTransfer = jobToTransfer.getEndTime().isBefore(powerShortageStart) || jobToTransfer.getEndTime().isEqual(powerShortageStart);

            if (willJobFinishBeforeTransfer) {
                logger.info("[{}] Job with id {} will finish before the transfer. Sending cancel transfer information ", guid, jobId);
                displayMessageArrow(myGreenAgent, myGreenAgent.getOwnerServer());
                myGreenAgent.send(prepareJobPowerShortageInformation(createPowerShortageJob(), myGreenAgent.getOwnerServer(), CANCELLED_TRANSFER_PROTOCOL));
            } else {
                logger.info("[{}] Finishing job with id {} on power shortage", guid, jobId);
                myGreenAgent.getPowerJobs().remove(jobToTransfer);
                if (jobToTransfer.getStartTime().isBefore(getCurrentTime())) {
                    myGreenAgent.manage().incrementFinishedJobs(jobToTransfer.getJobId());
                }
            }
        } else {
            logger.info("[{}] The job with id {} has finished before transfer.", guid, jobToTransfer.getJobId());
        }
    }

    @Override
    protected void handleFailure(ACLMessage failure) {
        if (myGreenAgent.getPowerJobs().containsKey(jobToTransfer)) {
            final String jobId = jobToTransfer.getJobId();
            logger.info("[{}] Transfer of job with id {} was unsuccessful!", guid, jobId);
            logger.info("[{}] Putting the job with id {} on hold", guid, jobId);
            myGreenAgent.getPowerJobs().replace(jobToTransfer, JobStatusEnum.ON_HOLD);
            myGreenAgent.manage().updateGreenSourceGUI();
        } else {
            logger.info("[{}] The job with id {} has finished before putting it on hold.", guid, jobToTransfer.getJobId());
        }
    }

    private PowerShortageJob createPowerShortageJob() {
        return ImmutablePowerShortageJob.builder()
                .powerShortageStart(powerShortageStart)
                .jobInstanceId(JobMapper.mapToJobInstanceId(jobToTransfer))
                .build();
    }
}
