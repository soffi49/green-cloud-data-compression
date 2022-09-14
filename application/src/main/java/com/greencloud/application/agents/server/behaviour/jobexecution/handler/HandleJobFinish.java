package com.greencloud.application.agents.server.behaviour.jobexecution.handler;

import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_FINISH_LOG;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.domain.job.Job;
import com.greencloud.application.domain.job.JobStatusEnum;
import com.greencloud.application.utils.TimeUtils;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour handles job execution finish
 */
public class HandleJobFinish extends WakerBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(HandleJobFinish.class);

	private final Job jobToExecute;
	private final ServerAgent myServerAgent;
	private final String guid;
	private final boolean informCNA;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent     agent that is executing the behaviour
	 * @param endTime   time when the behaviour should be executed
	 * @param job       job which execution is to be finished
	 * @param informCNA flag indicating whether cloud network should be informed about the job finish
	 */
	private HandleJobFinish(Agent agent, Date endTime, final Job job, final boolean informCNA) {
		super(agent, endTime);
		this.jobToExecute = job;
		this.myServerAgent = (ServerAgent) agent;
		this.informCNA = informCNA;
		this.guid = myServerAgent.getName();
	}

	/**
	 * Method calculates the time after which the job execution should finish.
	 *
	 * @param serverAgent agent that will execute the behaviour
	 * @param jobToFinish job which execution is to be terminated
	 * @param informCNA   flag indicating whether cloud network should be informed about the job finish
	 * @return behaviour to be run
	 */
	public static HandleJobFinish createFor(final ServerAgent serverAgent, final Job jobToFinish,
			final boolean informCNA) {
		final Instant endTime = TimeUtils.getCurrentTime().isAfter(jobToFinish.getEndTime()) ?
				TimeUtils.getCurrentTime() :
				jobToFinish.getEndTime();
		return new HandleJobFinish(serverAgent, Date.from(endTime), jobToFinish, informCNA);
	}

	/**
	 * Method performs postpone actions executed upon job finish.
	 * It sends the messages to the Green Source Agent and the Cloud Network Agent
	 * informing that the job execution has finished as well as it updates the server's state
	 */
	@Override
	protected void onWake() {
		if (Objects.nonNull(myServerAgent.getServerJobs().get(jobToExecute)) && JobStatusEnum.ACCEPTED_JOB_STATUSES.contains(
				myServerAgent.getServerJobs().get(jobToExecute))) {
			logger.info(JOB_FINISH_LOG, guid, jobToExecute.getJobId(), jobToExecute.getEndTime());
			myServerAgent.manage().finishJobExecution(jobToExecute, informCNA);
		}
	}
}
