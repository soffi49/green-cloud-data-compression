package agents.greenenergy.behaviour.powershortage.announcer;

import static common.AlgorithmUtils.findJobsWithinPower;
import static common.GUIUtils.displayMessageArrow;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageInformation;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.handler.SchedulePowerShortage;
import domain.job.ImmutablePowerShortageTransfer;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.job.PowerShortageTransfer;
import jade.core.behaviours.OneShotBehaviour;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour sends the information to the server that the green source will have unexpected power shortage
 * at the given time
 */
public class AnnounceSourcePowerShortage extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(AnnounceSourcePowerShortage.class);

    private final OffsetDateTime shortageStartTime;
    private final int maxAvailablePower;
    private final GreenEnergyAgent myGreenAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent           agent executing the behaviour
     * @param shortageStartTime start time when the power shortage will happen
     * @param maxAvailablePower power available during the power shortage
     */
    public AnnounceSourcePowerShortage(GreenEnergyAgent myAgent, OffsetDateTime shortageStartTime, int maxAvailablePower) {
        super(myAgent);
        this.shortageStartTime = shortageStartTime;
        this.maxAvailablePower = maxAvailablePower;
        this.myGreenAgent = myAgent;
    }

    /**
     * Method which is responsible for sending the information about the detected power shortage to the parent server.
     * In the message, the list of jobs that cannot be executed by the green source is passed along with the
     * start time of the power shortage
     */
    @Override
    public void action() {
        logger.info("[{}] !!!!! Power shortage was detected! Power shortage will happen at: {}", myGreenAgent.getName(), shortageStartTime);
        final List<PowerJob> affectedJobs = getAffectedPowerJobs();
        if (affectedJobs.isEmpty()) {
            logger.info("[{}] Power shortage won't affect any jobs", myGreenAgent.getName());
            myGreenAgent.addBehaviour(SchedulePowerShortage.createFor(shortageStartTime, maxAvailablePower, myGreenAgent));
        } else {
            logger.info("[{}] Sending power shortage information", myGreenAgent.getName());
            final List<PowerJob> jobsToKeep = findJobsWithinPower(affectedJobs, maxAvailablePower, PowerJob.class);
            final List<PowerJob> jobsToTransfer = affectedJobs.stream().filter(job -> !jobsToKeep.contains(job)).toList();
            final PowerShortageTransfer powerShortageTransfer = ImmutablePowerShortageTransfer.builder()
                    .jobList(jobsToTransfer)
                    .startTime(shortageStartTime)
                    .build();
            createNewJobInstances(jobsToTransfer);
            displayMessageArrow(myGreenAgent, myGreenAgent.getOwnerServer());
            myGreenAgent.send(preparePowerShortageInformation(powerShortageTransfer, myGreenAgent.getOwnerServer()));
            myGreenAgent.addBehaviour(SchedulePowerShortage.createFor(powerShortageTransfer, maxAvailablePower, myGreenAgent));
        }
    }

    private void createNewJobInstances(final List<PowerJob> affectedJobs) {
        affectedJobs.forEach(powerJob -> myGreenAgent.manage().divideJobForPowerShortage(powerJob, shortageStartTime));
    }

    private List<PowerJob> getAffectedPowerJobs() {
        return myGreenAgent.getPowerJobs().keySet().stream()
                .filter(job -> shortageStartTime.isBefore(job.getEndTime()) && !myGreenAgent.getPowerJobs().get(job).equals(JobStatusEnum.PROCESSING))
                .toList();
    }
}
