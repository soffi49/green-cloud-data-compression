package agents.client.behaviour;

import static agents.client.ClientAgentConstants.JOB_RETRY_MINUTES_ADJUSTMENT;
import static common.TimeUtils.convertToSimulationTime;
import static common.TimeUtils.getCurrentTime;

import agents.client.ClientAgent;
import domain.job.ImmutableJob;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleRetry extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleRetry.class);

    private final ClientAgent myClientAgent;
    private final Job job;

    public ScheduleRetry(Agent agent, long timeout, Job job) {
        super(agent, timeout);
        this.job = job;
        this.myClientAgent = (ClientAgent) agent;
    }

    @Override
    protected void onWake() {
        myAgent.addBehaviour(prepareStartingBehaviour(job));
        logger.info("Retrying to request job execution with id {}", job.getJobId());
    }

    private SequentialBehaviour prepareStartingBehaviour(final Job job) {
        var jobForRetry = ImmutableJob.builder()
            .jobId(job.getJobId())
            .startTime(job.getStartTime().plus(Duration.of(JOB_RETRY_MINUTES_ADJUSTMENT, ChronoUnit.MINUTES)))
            .endTime(job.getEndTime().plus(Duration.of(JOB_RETRY_MINUTES_ADJUSTMENT, ChronoUnit.MINUTES)))
            .power(job.getPower())
            .clientIdentifier(job.getClientIdentifier())
            .build();
        prepareSimulatedTimes(jobForRetry.getStartTime(), jobForRetry.getEndTime());
        var startingBehaviour = new SequentialBehaviour(myAgent);
        startingBehaviour.addSubBehaviour(new FindCloudNetworkAgents());
        startingBehaviour.addSubBehaviour(new RequestJobExecution(myAgent, null, jobForRetry));
        return startingBehaviour;
    }

    private void prepareSimulatedTimes(final OffsetDateTime startTime, final OffsetDateTime endTime) {
        final OffsetDateTime currentTime = getCurrentTime();
        final long expectedJobStart = convertToSimulationTime(ChronoUnit.SECONDS.between(currentTime, startTime));
        final long expectedJobEnd = convertToSimulationTime(ChronoUnit.SECONDS.between(currentTime, endTime));
        myClientAgent.setSimulatedJobStart(currentTime.plus(expectedJobStart, ChronoUnit.MILLIS));
        myClientAgent.setSimulatedJobEnd(currentTime.plus(expectedJobEnd, ChronoUnit.MILLIS));
    }
}