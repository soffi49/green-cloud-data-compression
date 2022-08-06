package agents.client.behaviour;

import static agents.client.ClientAgentConstants.JOB_RETRY_MINUTES_ADJUSTMENT;
import static common.TimeUtils.convertToSimulationTime;

import agents.client.ClientAgent;
import agents.client.behaviour.df.FindCloudNetworkAgents;
import domain.job.ImmutableJob;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;

import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleJobRequestRetry extends WakerBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleJobRequestRetry.class);

	private final ClientAgent myClientAgent;
	private final Job job;

	public ScheduleJobRequestRetry(Agent agent, long timeout, Job job) {
		super(agent, timeout);
		this.job = job;
		this.myClientAgent = (ClientAgent) agent;
	}

	@Override
	protected void onWake() {
		myAgent.addBehaviour(prepareStartingBehaviour(job));
		logger.info("Retrying to request job execution with id {}", job.getJobId());
		myAgent.removeBehaviour(this);
	}

	private SequentialBehaviour prepareStartingBehaviour(final Job job) {
		final long simulationAdjustment = convertToSimulationTime((long) JOB_RETRY_MINUTES_ADJUSTMENT * 60);
		myClientAgent.setSimulatedJobStart(
				myClientAgent.getSimulatedJobStart().plus(simulationAdjustment, ChronoUnit.MILLIS));
		myClientAgent.setSimulatedJobEnd(
				myClientAgent.getSimulatedJobEnd().plus(simulationAdjustment, ChronoUnit.MILLIS));

		var jobForRetry = ImmutableJob.builder()
				.jobId(job.getJobId())
				.startTime(myClientAgent.getSimulatedJobStart())
				.endTime(myClientAgent.getSimulatedJobEnd())
				.power(job.getPower())
				.clientIdentifier(job.getClientIdentifier())
				.build();
		var startingBehaviour = new SequentialBehaviour(myAgent);
		startingBehaviour.addSubBehaviour(new FindCloudNetworkAgents());
		startingBehaviour.addSubBehaviour(new RequestJobExecution(myAgent, null, jobForRetry));
		return startingBehaviour;
	}
}
