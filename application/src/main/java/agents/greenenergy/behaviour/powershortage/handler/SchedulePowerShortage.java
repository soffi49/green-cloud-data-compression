package agents.greenenergy.behaviour.powershortage.handler;

import static common.TimeUtils.getCurrentTime;

import agents.greenenergy.GreenEnergyAgent;
import com.gui.domain.nodes.GreenEnergyAgentNode;
import domain.job.PowerJob;
import domain.job.PowerShortageTransfer;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour schedules when the green source should update its internal state due to the power shortage
 */
public class SchedulePowerShortage extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(SchedulePowerShortage.class);
    private final GreenEnergyAgent myGreenEnergyAgent;
    private final List<PowerJob> jobsToHalt;
    private final int newMaximumCapacity;

    /**
     * Behaviour constructor.
     *
     * @param myAgent    agent executing the behaviour
     * @param timeout    timeout after which the jobs should be halted
     * @param jobsToHalt list of the jobs to be halted
     */
    private SchedulePowerShortage(Agent myAgent, long timeout, List<PowerJob> jobsToHalt, int newMaximumCapacity) {
        super(myAgent, timeout);
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
        this.jobsToHalt = jobsToHalt;
        this.newMaximumCapacity = newMaximumCapacity;
    }

    /**
     * Method creates the behaviour based on the passed parameters
     *
     * @param powerShortageTransfer transfer that should happen at the power shortage
     * @param newMaximumCapacity    updated capacity
     * @param greenEnergyAgent      agent executing the behaviour
     * @return behaviour scheduling the power shortage handling
     */
    public static SchedulePowerShortage createFor(final PowerShortageTransfer powerShortageTransfer, final int newMaximumCapacity, final GreenEnergyAgent greenEnergyAgent) {
        final long timeDifference = ChronoUnit.MILLIS.between(getCurrentTime(), powerShortageTransfer.getStartTime());
        final long timeOut = timeDifference < 0 ? 0 : timeDifference;
        return new SchedulePowerShortage(greenEnergyAgent, timeOut, powerShortageTransfer.getJobList(), newMaximumCapacity);
    }

    /**
     * Method creates the behaviour based on the passed parameters
     *
     * @param powerShortageTime  time when the power shortage should happen
     * @param newMaximumCapacity updated capacity
     * @param greenEnergyAgent   agent executing the behaviour
     * @return behaviour scheduling the power shortage handling
     */
    public static SchedulePowerShortage createFor(final OffsetDateTime powerShortageTime, final int newMaximumCapacity, final GreenEnergyAgent greenEnergyAgent) {
        final long timeDifference = ChronoUnit.MILLIS.between(getCurrentTime(), powerShortageTime);
        final long timeOut = timeDifference < 0 ? 0 : timeDifference;
        return new SchedulePowerShortage(greenEnergyAgent, timeOut, Collections.emptyList(), newMaximumCapacity);
    }

    /**
     * Method updates the internal state of the green source when the power shortage happens. It changes the value of the maximum
     * capacity as well as prints the information for (if there are some) jobs which will cause the power overflow that they are on hold
     */
    @Override
    protected void onWake() {
        jobsToHalt.forEach(jobToHalt -> {
            if (myGreenEnergyAgent.getPowerJobs().containsKey(jobToHalt)) {
                logger.info("[{}] Putting job {} on hold", myGreenEnergyAgent.getName(), jobToHalt.getJobId());
            }
        });
        myGreenEnergyAgent.setMaximumCapacity(newMaximumCapacity);
        ((GreenEnergyAgentNode) myGreenEnergyAgent.getAgentNode()).updateMaximumCapacity(newMaximumCapacity);
    }
}
