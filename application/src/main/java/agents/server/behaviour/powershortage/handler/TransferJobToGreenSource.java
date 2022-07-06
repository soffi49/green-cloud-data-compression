package agents.server.behaviour.powershortage.handler;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateServerState;
import static common.TimeUtils.getCurrentTime;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.JobStatusMessageFactory.prepareJobStartedMessage;

import agents.server.ServerAgent;
import agents.server.behaviour.FinishJobExecution;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Behaviour responsible for transferring a job to a new green source
 */
public class TransferJobToGreenSource extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(TransferJobToGreenSource.class);

    private final ServerAgent myServerAgent;
    private final String jobId;
    private final AID newGreenSource;
    private final OffsetDateTime shortageStartTime;

    /**
     * Behaviour constructor.
     *
     * @param myAgent agent executing the behaviour
     * @param timeout timeout after which the job should be transferred
     * @param jobId   unique identifier of the job
     */
    private TransferJobToGreenSource(Agent myAgent, long timeout, String jobId, AID newGreenSource, OffsetDateTime shortageStartTime) {
        super(myAgent, timeout);
        this.myServerAgent = (ServerAgent) myAgent;
        this.jobId = jobId;
        this.newGreenSource = newGreenSource;
        this.shortageStartTime = shortageStartTime;
    }

    /**
     * Method creates the behaviour based on the passed arguments
     *
     * @param serverAgent       server executing the behaviour
     * @param jobId             unique job identifier
     * @param shortageStartTime time when the power shortage starts
     * @param newGreenSource    green source which will execute the job after power shortage
     * @return behaviour which transfer the jobs between green sources
     */
    public static TransferJobToGreenSource createFor(final ServerAgent serverAgent, final String jobId, final OffsetDateTime shortageStartTime, final AID newGreenSource) {
        final long timeDifference = ChronoUnit.MILLIS.between(getCurrentTime(), shortageStartTime);
        final long timeOut = timeDifference < 0 ? 0 : timeDifference;
        return new TransferJobToGreenSource(serverAgent, timeOut, jobId, newGreenSource, shortageStartTime);
    }

    /**
     * Method transfers the job between green sources. Firstly it finishes the execution of the job in old green source, then
     * it updates the server internal state, and finally it transfers the job to the new green source
     */
    @Override
    protected void onWake() {
        final Job jobToStart = myServerAgent.getJobByIdAndStartDate(jobId, shortageStartTime);
        final Job jobToFinish = myServerAgent.getJobByIdAndEndDate(jobId, shortageStartTime);
        if (Objects.nonNull(jobToStart) && Objects.nonNull(jobToFinish)) {
            finishJobExecutionInOldGreenSource(jobToFinish);
            transferJobToNewGreenSource();
            startJobExecutionInNewGreenSource(jobToStart);
        }
    }

    private void transferJobToNewGreenSource() {
        logger.info("[{}] Transferring the job with id {} to a new green source", myServerAgent.getName(), jobId);
        myServerAgent.getGreenSourceForJobMap().replace(jobId, newGreenSource);
        myServerAgent.getServerJobs().replace(myServerAgent.getJobByIdAndStartDate(jobId, shortageStartTime), JobStatusEnum.IN_PROGRESS);
        updateServerState(myServerAgent);
    }

    private void startJobExecutionInNewGreenSource(final Job jobToExecute) {
        logger.info("[{}] Start executing the job in the new green source", myServerAgent.getName());
        if (!myServerAgent.getServerJobs().get(jobToExecute).equals(JobStatusEnum.IN_PROGRESS)) {
            myServerAgent.getServerJobs().replace(jobToExecute, JobStatusEnum.IN_PROGRESS);
        }
        final ACLMessage startedJobMessage = prepareJobStartedMessage(jobId, shortageStartTime, List.of(newGreenSource));
        displayMessageArrow(myServerAgent, newGreenSource);
        myAgent.send(startedJobMessage);
        myAgent.addBehaviour(FinishJobExecution.createFor(myServerAgent, jobToExecute, true));
    }

    private void finishJobExecutionInOldGreenSource(final Job jobToFinish) {
        logger.info("[{}] Finished executing the job in old green source", myServerAgent.getName());
        final List<AID> receivers = List.of(myServerAgent.getGreenSourceForJobMap().get(jobToFinish.getJobId()));
        final ACLMessage finishJobMessage = prepareFinishMessage(jobToFinish.getJobId(), jobToFinish.getStartTime(), receivers);
        myServerAgent.getServerJobs().remove(jobToFinish);
        displayMessageArrow(myServerAgent, receivers);
        myServerAgent.send(finishJobMessage);
    }
}
