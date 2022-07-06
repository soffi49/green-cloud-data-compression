package agents.server.behaviour;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateServerState;
import static common.TimeUtils.getCurrentTime;
import static messages.domain.JobStatusMessageFactory.prepareJobStartedMessage;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StartJobExecution extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(StartJobExecution.class);
    private final ServerAgent myServerAgent;
    private final JobInstanceIdentifier jobToExecute;
    private final boolean informCNA;

    /**
     * Behaviour constructor.
     *
     * @param agent     agent that is executing the behaviour
     * @param timeOut   time after which the job will be executed
     * @param jobId     identifier of the job that is to be executed
     * @param informCNA flag indicating whether the cloud network should be informed about job start
     */
    private StartJobExecution(Agent agent, long timeOut, final JobInstanceIdentifier jobId, final boolean informCNA) {
        super(agent, timeOut);
        this.jobToExecute = jobId;
        myServerAgent = (ServerAgent) agent;
        this.informCNA = informCNA;
    }

    /**
     * Method which is responsible for creating the behaviour. It calculates the time after which
     * the job execution will start. For testing purposes 1h = 2s. If the provided time is later than
     * the current time then the job execution will start immediately
     *
     * @param serverAgent agent that will execute the behaviour
     * @param jobId       identifier of the job that is to be executed
     * @param informCNA   flag indicating whether the cloud network should be informed about job start
     * @return behaviour to be run
     */
    public static StartJobExecution createFor(final ServerAgent serverAgent, final JobInstanceIdentifier jobId, final boolean informCNA) {
        final long timeDifference = ChronoUnit.MILLIS.between(getCurrentTime(), jobId.getStartTime());
        final long timeOut = timeDifference < 0 ? 0 : timeDifference;
        return new StartJobExecution(serverAgent, timeOut, jobId, informCNA);
    }

    /**
     * Method starts the execution of the job. It updates the server state, then sends the information that the execution has started to the
     * Green Source Agent and the Cloud Network. Finally, it starts the behaviour responsible for informing about job
     * execution finish.
     */
    @Override
    protected void onWake() {
        final Job job = myServerAgent.getJobByIdAndStartDate(jobToExecute.getJobId(), jobToExecute.getStartTime());
        if(Objects.nonNull(job)) {
            logger.info("[{}] Start executing the job for {}", myAgent.getName(), job.getClientIdentifier());
            myServerAgent.getServerJobs().replace(job, JobStatusEnum.IN_PROGRESS);
            updateServerState(myServerAgent);
            final List<AID> receivers = informCNA ? List.of(myServerAgent.getGreenSourceForJobMap().get(job.getJobId()), myServerAgent.getOwnerCloudNetworkAgent()) :
                    Collections.singletonList(myServerAgent.getGreenSourceForJobMap().get(job.getJobId()));
            final ACLMessage startedJobMessage = prepareJobStartedMessage(job.getJobId(), job.getStartTime(), receivers);
            displayMessageArrow(myServerAgent, receivers);
            myAgent.send(startedJobMessage);
            myAgent.addBehaviour(FinishJobExecution.createFor(myServerAgent, job, true));
        }
    }
}
