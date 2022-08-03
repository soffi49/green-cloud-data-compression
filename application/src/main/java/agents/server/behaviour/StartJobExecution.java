package agents.server.behaviour;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static messages.domain.JobStatusMessageFactory.prepareJobStartedMessage;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StartJobExecution extends WakerBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(StartJobExecution.class);
	private final ServerAgent myServerAgent;
	private final Job jobToExecute;
	private final boolean informCNAStart;
	private final boolean informCNAFinish;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent           agent that is executing the behaviour
	 * @param startDate       time when the job execution should begin
	 * @param job             job which execution should start
	 * @param informCNAStart  flag indicating whether the cloud network should be informed about job start
	 * @param informCNAFinish flag indicating whether the cloud network should be informed about job finish
	 */
	private StartJobExecution(Agent agent, Date startDate,
			final Job job,
			final boolean informCNAStart,
			final boolean informCNAFinish) {
		super(agent, startDate);
		this.jobToExecute = job;
		myServerAgent = (ServerAgent) agent;
		this.informCNAStart = informCNAStart;
		this.informCNAFinish = informCNAFinish;
	}

	/**
	 * Method which is responsible for creating the behaviour. It calculates the time after which the job execution will
	 * start. For testing purposes 1h = 2s. If the provided time is later than the current time then the job execution
	 * will start immediately
	 *
	 * @param serverAgent     agent that will execute the behaviour
	 * @param job             job to execute
	 * @param informCNAStart  flag indicating whether the cloud network should be informed about job start
	 * @param informCNAFinish flag indicating whether the cloud network should be informed about job finish
	 * @return behaviour to be run
	 */
	public static StartJobExecution createFor(final ServerAgent serverAgent,
			final Job job,
			final boolean informCNAStart,
			final boolean informCNAFinish) {
		final OffsetDateTime startDate = getCurrentTime().isAfter(job.getStartTime()) ?
				getCurrentTime() :
				job.getStartTime();
		return new StartJobExecution(serverAgent, Date.from(startDate.toInstant()), job, informCNAStart,
				informCNAFinish);
	}

	/**
	 * Method starts the execution of the job. It updates the server state, then sends the information that the
	 * execution has started to the Green Source Agent and the Cloud Network. Finally, it starts the behaviour
	 * responsible for informing about job execution finish.
	 */
	@Override
	protected void onWake() {
		if (!myServerAgent.getGreenSourceForJobMap().containsKey(jobToExecute.getJobId())) {
			logger.info("[{}] Job execution couldn't start: there is no green source for the job {}",
					myServerAgent.getLocalName(), jobToExecute.getJobId());
		} else if (!myServerAgent.getServerJobs().containsKey(jobToExecute)) {
			logger.info("[{}] Job execution couldn't start: job {} is not present", myServerAgent.getLocalName(),
					jobToExecute.getJobId());
		} else {
			if (informCNAStart) {
				logger.info("[{}] Start executing the job for {}", myAgent.getName(),
						jobToExecute.getClientIdentifier());
			} else {
				logger.info("[{}] Start executing the job for {} without informing CNA", myAgent.getName(),
						jobToExecute.getClientIdentifier());
			}
			if (myServerAgent.getServerJobs().get(jobToExecute).equals(JobStatusEnum.ACCEPTED)) {
				myServerAgent.getServerJobs().replace(jobToExecute, JobStatusEnum.IN_PROGRESS);
			}
			final List<AID> receivers = informCNAStart ?
					List.of(myServerAgent.getGreenSourceForJobMap().get(jobToExecute.getJobId()),
							myServerAgent.getOwnerCloudNetworkAgent()) :
					Collections.singletonList(myServerAgent.getGreenSourceForJobMap().get(jobToExecute.getJobId()));
			final ACLMessage startedJobMessage = prepareJobStartedMessage(jobToExecute.getJobId(),
					jobToExecute.getStartTime(), receivers);
			displayMessageArrow(myServerAgent, receivers);
			myServerAgent.manage().incrementStartedJobs(jobToExecute.getJobId());
			myAgent.send(startedJobMessage);
			myAgent.addBehaviour(FinishJobExecution.createFor(myServerAgent, jobToExecute, informCNAFinish));
			myAgent.removeBehaviour(this);
		}
	}
}
