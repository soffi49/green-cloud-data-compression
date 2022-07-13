package agents.greenenergy.behaviour.powershortage.announcer;

import static common.AlgorithmUtils.findJobsWithinPower;
import static common.GUIUtils.displayMessageArrow;
import static domain.job.JobStatusEnum.JOB_ON_HOLD;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageTransferRequest;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.handler.SchedulePowerShortage;
import agents.greenenergy.behaviour.powershortage.transfer.RequestPowerJobTransfer;
import common.mapper.JobMapper;
import domain.job.ImmutablePowerShortageTransfer;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.job.PowerShortageTransfer;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

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
            jobsToTransfer.forEach(powerJob -> {
                final PowerJob jobToTransfer = myGreenAgent.manage().divideJobForPowerShortage(powerJob, shortageStartTime);
                final ACLMessage transferMessage = preparePowerShortageTransferRequest(JobMapper.mapToPowerShortageJob(powerJob, shortageStartTime), myGreenAgent.getOwnerServer());
                displayMessageArrow(myGreenAgent, myGreenAgent.getOwnerServer());
                myGreenAgent.addBehaviour(new RequestPowerJobTransfer(myGreenAgent, transferMessage, jobToTransfer, shortageStartTime));
            });
            myGreenAgent.addBehaviour(SchedulePowerShortage.createFor(preparePowerShortageTransfer(jobsToTransfer), maxAvailablePower, myGreenAgent));
        }
    }

    private PowerShortageTransfer preparePowerShortageTransfer(final List<PowerJob> powerJobs) {
        return ImmutablePowerShortageTransfer.builder()
                .jobList(powerJobs)
                .startTime(shortageStartTime)
                .build();
    }

    private List<PowerJob> getAffectedPowerJobs() {
        final EnumSet<JobStatusEnum> notAffectedJobs =  EnumSet.copyOf(JOB_ON_HOLD);
        notAffectedJobs.add(JobStatusEnum.PROCESSING);
        return myGreenAgent.getPowerJobs().keySet().stream()
                .filter(job -> shortageStartTime.isBefore(job.getEndTime()) && !notAffectedJobs.contains(myGreenAgent.getPowerJobs().get(job)))
                .toList();
    }
}
