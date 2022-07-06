package agents.greenenergy.behaviour;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateGreenSourceState;
import static messages.domain.JobStatusMessageFactory.prepareManualFinishMessageForServer;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * Behaviour responsible for verifying if the job was finished at the correct time.
 */
public class FinishJobManually extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FinishJobManually.class);

    private final JobInstanceIdentifier jobInstanceId;
    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor.
     *
     * @param agent         agent which is executing the behaviour
     * @param timeout       timeout after which the job should finish
     * @param jobInstanceId unique job instance identifier
     */
    public FinishJobManually(Agent agent, long timeout, JobInstanceIdentifier jobInstanceId) {
        super(agent, timeout);
        this.myGreenEnergyAgent = (GreenEnergyAgent) agent;
        this.jobInstanceId = jobInstanceId;
    }

    /**
     * Method verifies if the job execution finished correctly. If there was no information about
     * job finish execution but the timout passed - the Green Source finishes the power supply manually and sends
     * the warning to the Server Agent.
     */
    @Override
    protected void onWake() {
        final PowerJob job = myGreenEnergyAgent.getJobByIdAndStartDate(jobInstanceId.getJobId(), jobInstanceId.getStartTime());
        if (Objects.nonNull(job) && myGreenEnergyAgent.getPowerJobs().containsKey(job) && myGreenEnergyAgent.getPowerJobs().get(job).equals(JobStatusEnum.IN_PROGRESS)) {
            logger.error("[{}] The power delivery should be finished! Finishing power delivery by hand.", myAgent.getName());
            myGreenEnergyAgent.getPowerJobs().remove(job);
            final Optional<Boolean> isJobFinished = Objects.isNull(myGreenEnergyAgent.getJobById(job.getJobId()))? Optional.empty() : Optional.of(true);
            updateGreenSourceState(myGreenEnergyAgent);
            displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getOwnerServer());
            myAgent.send(prepareManualFinishMessageForServer(jobInstanceId, myGreenEnergyAgent.getOwnerServer()));
        }
    }
}
