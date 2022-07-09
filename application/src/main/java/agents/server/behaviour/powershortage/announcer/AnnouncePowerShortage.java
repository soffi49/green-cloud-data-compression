package agents.server.behaviour.powershortage.announcer;

import static common.AlgorithmUtils.findJobsWithinPower;
import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageInformation;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import common.mapper.JobMapper;
import domain.job.ImmutablePowerShortageTransfer;
import domain.job.Job;
import domain.job.JobStatusEnum;
import domain.job.PowerShortageTransfer;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour sends the information to the cloud network that there is some power shortage and the
 * job cannot be executed by the server
 */
public class AnnouncePowerShortage extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncePowerShortage.class);

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
    public AnnouncePowerShortage(ServerAgent myAgent, OffsetDateTime shortageStartTime, int recalculatedAvailablePower) {
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
            final PowerShortageTransfer powerShortageTransfer = ImmutablePowerShortageTransfer.builder()
                    .jobList(jobsToDivide.stream().map(JobMapper::mapJobToPowerJob).toList())
                    .startTime(shortageStartTime)
                    .build();
            createNewJobInstances(jobsToDivide, shortageStartTime);
            displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
            myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(jobsToDivide, powerShortageTransfer.getStartTime(), myServerAgent, recalculatedAvailablePower));
            myServerAgent.send(preparePowerShortageInformation(powerShortageTransfer, myServerAgent.getOwnerCloudNetworkAgent()));
        }
    }

    private void createNewJobInstances(final List<Job> jobList, final OffsetDateTime shortageTime) {
        jobList.forEach(job -> {
            final AID greenSource = myServerAgent.getGreenSourceForJobMap().get(job.getJobId());
            myServerAgent.manage().divideJobForPowerShortage(job, shortageTime);
            displayMessageArrow(myServerAgent, greenSource);
            myServerAgent.send(prepareJobPowerShortageInformation(JobMapper.mapToJobInstanceId(job), shortageTime, greenSource, POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL));
        });
    }

    private List<Job> getAffectedPowerJobs() {
        return myServerAgent.getServerJobs().keySet().stream()
                .filter(job -> shortageStartTime.isBefore(job.getEndTime()) && !myServerAgent.getServerJobs().get(job).equals(JobStatusEnum.PROCESSING))
                .toList();
    }
}
