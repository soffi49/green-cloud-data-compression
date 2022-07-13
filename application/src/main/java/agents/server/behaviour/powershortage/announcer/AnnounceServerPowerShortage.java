package agents.server.behaviour.powershortage.announcer;

import static common.AlgorithmUtils.findJobsWithinPower;
import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ALERT_PROTOCOL;
import static domain.job.JobStatusEnum.ACTIVE_JOB_STATUSES;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageTransferRequest;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import agents.server.behaviour.powershortage.transfer.RequestJobTransferInCloudNetwork;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

/**
 * Behaviour sends the information to the cloud network that there is some power shortage and the
 * job cannot be executed by the server
 */
public class AnnounceServerPowerShortage extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(AnnounceServerPowerShortage.class);

    private final OffsetDateTime shortageStartTime;
    private final int recalculatedAvailablePower;
    private final ServerAgent myServerAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent                    agent executing the behaviour
     * @param shortageStartTime          start time when the power shortage will happen
     * @param recalculatedAvailablePower power available during the power shortage
     */
    public AnnounceServerPowerShortage(ServerAgent myAgent, OffsetDateTime shortageStartTime, int recalculatedAvailablePower) {
        super(myAgent);
        this.shortageStartTime = shortageStartTime;
        this.recalculatedAvailablePower = recalculatedAvailablePower;
        this.myServerAgent = myAgent;
    }

    /**
     * Method is responsible for announcing to the cloud network that there will be some power shortage
     * which cannot be handled by the server itself.
     */
    @Override
    public void action() {
        logger.info("[{}] !!!!! Power shortage was detected for server! Power shortage will happen at: {}", myServerAgent.getName(), shortageStartTime);
        final List<Job> affectedJobs = getAffectedPowerJobs();
        if (affectedJobs.isEmpty()) {
            logger.info("[{}] Power shortage won't affect any jobs", myServerAgent.getName());
            myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(affectedJobs, shortageStartTime, myServerAgent, recalculatedAvailablePower));
        } else {
            logger.info("[{}] Sending power shortage information to cloud network", myServerAgent.getName());
            final List<Job> jobsToKeep = findJobsWithinPower(affectedJobs, recalculatedAvailablePower, Job.class);
            final List<Job> jobsToDivide = affectedJobs.stream().filter(job -> !jobsToKeep.contains(job)).toList();
            jobsToDivide.forEach(job -> {
                logger.info("[{}] Requesting job {} transfer in cloud network", myServerAgent.getName(), job.getJobId());
                final Job jobToTransfer = myServerAgent.manage().divideJobForPowerShortage(job, shortageStartTime);
                final ACLMessage transferMessage = preparePowerShortageTransferRequest(JobMapper.mapToPowerShortageJob(job, shortageStartTime), myServerAgent.getOwnerCloudNetworkAgent());
                displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
                myServerAgent.addBehaviour(new RequestJobTransferInCloudNetwork(myServerAgent, transferMessage, null, JobMapper.mapToPowerShortageJob(jobToTransfer, shortageStartTime), true));
                informGreenSourceAboutPowerShortage(job, shortageStartTime);
            });
            myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(jobsToDivide, shortageStartTime, myServerAgent, recalculatedAvailablePower));
        }
    }

    private void informGreenSourceAboutPowerShortage(final Job job, final OffsetDateTime shortageTime) {
        final AID greenSource = myServerAgent.getGreenSourceForJobMap().get(job.getJobId());
        displayMessageArrow(myServerAgent, greenSource);
        myServerAgent.send(prepareJobPowerShortageInformation(JobMapper.mapToPowerShortageJob(job, shortageTime), greenSource, SERVER_POWER_SHORTAGE_ALERT_PROTOCOL));
    }

    private List<Job> getAffectedPowerJobs() {
        return myServerAgent.getServerJobs().keySet().stream()
                .filter(job -> shortageStartTime.isBefore(job.getEndTime()) && ACTIVE_JOB_STATUSES.contains(myServerAgent.getServerJobs().get(job)))
                .toList();
    }
}
