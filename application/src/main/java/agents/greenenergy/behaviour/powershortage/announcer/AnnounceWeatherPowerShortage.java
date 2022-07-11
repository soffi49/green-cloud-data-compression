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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnounceWeatherPowerShortage extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(AnnounceWeatherPowerShortage.class);

    private final PowerJob causingPowerJob;
    private final OffsetDateTime shortageStartTime;
    private final Double availablePower;
    private final GreenEnergyAgent myGreenAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent           agent executing the behaviour
     * @param causingPowerJob   during power check for this job weather power shortage occurred
     * @param shortageStartTime start time when the power shortage will happen
     * @param availablePower    power available during the power shortage
     */
    public AnnounceWeatherPowerShortage(GreenEnergyAgent myAgent, PowerJob causingPowerJob,
        OffsetDateTime shortageStartTime, Double availablePower) {
        super(myAgent);
        this.shortageStartTime = shortageStartTime;
        this.causingPowerJob = causingPowerJob;
        this.availablePower = availablePower;
        this.myGreenAgent = myAgent;
    }

    /**
     * Method which is responsible for sending the information about the detected power shortage to the parent server.
     * In the message, the list of jobs that cannot be executed by the green source is passed along with the start time
     * of the power shortage
     */
    @Override
    public void action() {
        logger.info("[{}] Weather-caused power shortage was detected!", myGreenAgent.getName());
        final List<PowerJob> affectedJobs = getAffectedPowerJobs();
        logger.info("[{}] Sending weather-caused power shortage information", myGreenAgent.getName());
        final List<PowerJob> jobsToKeep = findJobsWithinPower(affectedJobs, availablePower, PowerJob.class);
        final List<PowerJob> jobsToTransfer = affectedJobs.stream()
            .filter(job -> !jobsToKeep.contains(job))
            .collect(Collectors.toCollection(ArrayList::new));
        jobsToTransfer.add(causingPowerJob);
        final PowerShortageTransfer powerShortageTransfer = ImmutablePowerShortageTransfer.builder()
            .jobList(jobsToTransfer)
            .startTime(shortageStartTime)
            .build();
        createNewJobInstances(jobsToTransfer);
        displayMessageArrow(myGreenAgent, myGreenAgent.getOwnerServer());
        myGreenAgent.send(preparePowerShortageInformation(powerShortageTransfer, myGreenAgent.getOwnerServer()));
        myGreenAgent.addBehaviour(SchedulePowerShortage.createFor(powerShortageTransfer, myGreenAgent));
    }

    private void createNewJobInstances(final List<PowerJob> affectedJobs) {
        affectedJobs.forEach(powerJob -> myGreenAgent.manage().divideJobForPowerShortage(powerJob, shortageStartTime));
    }

    private List<PowerJob> getAffectedPowerJobs() {
        return myGreenAgent.getPowerJobs().keySet().stream()
            .filter(job -> !job.equals(causingPowerJob))
            .filter(job -> shortageStartTime.isBefore(job.getEndTime()) && !myGreenAgent.getPowerJobs().get(job).equals(
                JobStatusEnum.PROCESSING))
            .toList();
    }
}
