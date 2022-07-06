package agents.server.behaviour;

import static agents.server.ServerConstants.IN_PROGRESS_STATE;

import agents.server.ServerAgent;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Behaviour responsible for returning to the CNA and GreenSource the information that the job execution has finished
 */
public class FinishJobExecution extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FinishJobExecution.class);
    private final Job jobToExecute;
    private final ServerAgent myServerAgent;
    private final boolean informCNA;

    /**
     * Behaviour constructor.
     *
     * @param agent     agent that is executing the behaviour
     * @param timeOut   time during which the job is being executed
     * @param job       job that is being executed
     * @param informCNA flag indicating whether cloud network should be informed about the job finish
     */
    private FinishJobExecution(Agent agent, long timeOut, final Job job, final boolean informCNA) {
        super(agent, timeOut);
        this.jobToExecute = job;
        this.myServerAgent = (ServerAgent) agent;
        this.informCNA = informCNA;
    }

    /**
     * Method which is responsible for creating the behaviour. It calculates the time during which
     * the job will be executed. For testing purposes 1h = 2s
     *
     * @param serverAgent  agent that will execute the behaviour
     * @param jobToExecute job that will be executed
     * @param informCNA    flag indicating whether cloud network should be informed about the job finish
     * @return behaviour to be run
     */
    public static FinishJobExecution createFor(final ServerAgent serverAgent, final Job jobToExecute, final boolean informCNA) {
        final long timeOut = ChronoUnit.MILLIS.between(jobToExecute.getStartTime(), jobToExecute.getEndTime());
        return new FinishJobExecution(serverAgent, timeOut, jobToExecute, informCNA);
    }

    /**
     * Method which runs after the job execution finishes (the time-out is reached). It is responsible
     * for sending the information to the Green Source Agent and the Cloud Network Agent
     * informing that the job execution has finished. It also updates the server state.
     */
    @Override
    protected void onWake() {
        if (Objects.nonNull(myServerAgent.getServerJobs().get(jobToExecute)) && IN_PROGRESS_STATE.contains(myServerAgent.getServerJobs().get(jobToExecute))) {
            logger.info("[{}] Finished executing the job for {}", myAgent.getName(), jobToExecute.getClientIdentifier());
            myServerAgent.finishJobExecution(jobToExecute, informCNA);
        }
    }
}
