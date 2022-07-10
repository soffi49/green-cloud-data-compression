package agents.server.behaviour.powershortage.handler;

import static common.TimeUtils.getCurrentTime;

import agents.server.ServerAgent;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for using the backup power to finish job execution in case when no other energy source is available
 */
public class HandleServerPowerShortage extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleServerPowerShortage.class);
    private final ServerAgent myServerAgent;
    private final List<Job> jobsToExecute;
    private final Integer newMaximumCapacity;

    /**
     * Behaviour constructor.
     *
     * @param myAgent            agent executing the behaviour
     * @param shortageTime       time when the power shortage starts
     * @param jobsToExecute      list of the jobs to be finished
     * @param newMaximumCapacity maximum capacity value during power shortage
     *                           (if null then it means that shortage does not concern server directly)
     */
    private HandleServerPowerShortage(Agent myAgent, Date shortageTime, List<Job> jobsToExecute, final Integer newMaximumCapacity) {
        super(myAgent, shortageTime);
        this.myServerAgent = (ServerAgent) myAgent;
        this.jobsToExecute = jobsToExecute;
        this.newMaximumCapacity = newMaximumCapacity;
    }

    /**
     * Method creates the behaviour based on the passed parameters
     *
     * @param jobsToExecute   list of the jobs to be finished
     * @param serverAgent     agent executing the behaviour
     * @param newMaximumPower maximum power value during power shortage
     * @return behaviour scheduling the power shortage handling
     */
    public static HandleServerPowerShortage createFor(final List<Job> jobsToExecute, final OffsetDateTime shortageStartTime,
                                                      final ServerAgent serverAgent, final Integer newMaximumPower) {
        final OffsetDateTime startTime = getCurrentTime().isAfter(shortageStartTime) ? getCurrentTime() : shortageStartTime;
        return new HandleServerPowerShortage(serverAgent, Date.from(startTime.toInstant()), jobsToExecute, newMaximumPower);
    }

    /**
     * Method prints the information about the jobs that need to be halted that they are supplied with backup energy
     **/
    @Override
    protected void onWake() {
        jobsToExecute.forEach(job -> {
            if (myServerAgent.getServerJobs().containsKey(job)) {
                logger.info("[{}] Supplying job with id {} using backup power", myServerAgent.getName(), job.getJobId());
                myServerAgent.manage().updateServerGUI();
            }
        });
        if (Objects.nonNull(newMaximumCapacity)) {
            myServerAgent.manage().updateMaximumCapacity(newMaximumCapacity);
        }
        myAgent.removeBehaviour(this);
    }
}
