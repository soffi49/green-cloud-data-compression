package com.greencloud.application.agents.server.behaviour.jobexecution.handler;

import static com.greencloud.application.agents.server.behaviour.jobexecution.handler.logs.JobHandlingHandlerLog.JOB_FINISH_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED_JOB_STATUSES;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour handles job execution finish
 */
public class HandleJobFinish extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleJobFinish.class);

	private final ClientJob jobToExecute;
	private final ServerAgent myServerAgent;
	private final boolean informCNA;

	private HandleJobFinish(final ServerAgent agent, final Date endTime, final ClientJob job, final boolean informCNA) {
		super(agent, endTime);

		this.jobToExecute = job;
		this.myServerAgent = agent;
		this.informCNA = informCNA;
	}

	/**
	 * Method calculates the time after which the job execution should finish.
	 *
	 * @param serverAgent agent that will execute the behaviour
	 * @param job         job which execution is to be terminated
	 * @param informCNA   flag indicating whether cloud network should be informed about the job finish
	 * @return HandleJobFinish
	 */
	public static HandleJobFinish createFor(final ServerAgent serverAgent, final ClientJob job,
			final boolean informCNA) {
		final Instant endTime = getCurrentTime().isAfter(job.getEndTime()) ? getCurrentTime() : job.getEndTime();
		return new HandleJobFinish(serverAgent, Date.from(endTime), job, informCNA);
	}

	/**
	 * Method performs postpone actions executed upon job finish.
	 * It sends the messages to the Green Source Agent and the Cloud Network Agent
	 * informing that the job execution has finished as well as it updates the server's state
	 */
	@Override
	protected void onWake() {
		MDC.put(MDC_JOB_ID, jobToExecute.getJobId());
		if (myServerAgent.getServerJobs().containsKey(jobToExecute) &&
				ACCEPTED_JOB_STATUSES.contains(myServerAgent.getServerJobs().get(jobToExecute))) {
			logger.info(JOB_FINISH_LOG, jobToExecute.getJobId(), jobToExecute.getEndTime());
			myServerAgent.manage().finishJobExecution(jobToExecute, informCNA);
		}
	}
}
