package agents.greenenergy.behaviour.powershortage.announcer;

import static common.TimeUtils.getCurrentTime;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageFinishInformation;

import agents.greenenergy.GreenEnergyAgent;
import common.mapper.JobMapper;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.behaviours.OneShotBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Behaviour is responsible for announcing that the power shortage has finished at the given moment
 */
public class AnnouncePowerShortageFinish extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncePowerShortageFinish.class);
    private final GreenEnergyAgent myGreenAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent agent executing the behaviour
     */
    public AnnouncePowerShortageFinish(GreenEnergyAgent myAgent) {
        super(myAgent);
        this.myGreenAgent = myAgent;
    }

    /**
     * Method which is responsible for listening for the information that the power shortage has
     * finished and that the methods which were kept on hold can now be supplied with power
     */
    @Override
    public void action() {
        logger.info(
                "[{}] !!!!! Power shortage has finished! Supplying jobs with green power ",
                myGreenAgent.getName());
        final List<PowerJob> jobsOnHold = getJobsOnHold();
        if (jobsOnHold.isEmpty()) {
            logger.info("[{}] There are no jobs which were on hold. Updating the maximum power", myGreenAgent.getName());
        } else {
            logger.info("[{}] Changing the statuses of the jobs and informing the Server Agent", myGreenAgent.getLocalName());
            jobsOnHold.forEach(
                    powerJob -> {
                        if (myGreenAgent.getPowerJobs().containsKey(powerJob)) {
                            logger.info("[{}] Changing the status of the job {}", myGreenAgent.getLocalName(), powerJob.getJobId());
                            if (powerJob.getStartTime().isAfter(getCurrentTime())) {
                                myGreenAgent.getPowerJobs().replace(powerJob, JobStatusEnum.ACCEPTED);
                            } else {
                                myGreenAgent.getPowerJobs().replace(powerJob, JobStatusEnum.IN_PROGRESS);
                            }
                            myGreenAgent.manage().updateGreenSourceGUI();
                            myGreenAgent.send(preparePowerShortageFinishInformation(JobMapper.mapToJobInstanceId(powerJob), myGreenAgent.getOwnerServer()));
                        }
                    });
        }
        myGreenAgent.setMaximumCapacity(myGreenAgent.getInitialMaximumCapacity());
    }

    private List<PowerJob> getJobsOnHold() {
        return myGreenAgent.getPowerJobs().entrySet().stream()
                .filter(
                        job ->
                                job.getValue().equals(JobStatusEnum.ON_HOLD)
                                        && job.getKey().getEndTime().isAfter(getCurrentTime()))
                .map(Map.Entry::getKey)
                .toList();
    }
}
