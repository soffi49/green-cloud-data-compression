package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.handler;

import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.handler.logs.JobHandlingHandlerLog.JOB_DELAY_LOG;
import static com.greencloud.application.agents.cloudnetwork.constants.CloudNetworkAgentConstants.MAX_ERROR_IN_JOB_START;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.JobUtils.getJobById;
import static com.greencloud.application.utils.TimeUtils.alignStartTimeToCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.InitiateJobStartCheck;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.behaviours.WakerBehaviour;

/**
 * Behaviour verifies if the job execution has started on time
 */
public class HandleDelayedJob extends WakerBehaviour {

	private static final Logger logger = getLogger(HandleDelayedJob.class);

	private final String jobId;
	private final Instant jobStart;
	private final CloudNetworkAgent myCloudNetworkAgent;

	protected HandleDelayedJob(final CloudNetworkAgent agent, final Date startTime, final String jobId) {
		super(agent, startTime);
		this.myCloudNetworkAgent = agent;
		this.jobId = jobId;
		this.jobStart = startTime.toInstant();
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param cloudNetworkAgent agent executing the behaviour
	 * @param job               job of interest
	 * @return HandleDelayedJob
	 */
	public static HandleDelayedJob create(final CloudNetworkAgent cloudNetworkAgent, final ClientJob job) {
		final Instant startTime = alignStartTimeToCurrentTime(job.getStartTime());
		final Date expectedJobStart = Date.from(startTime.plusSeconds(MAX_ERROR_IN_JOB_START));

		return new HandleDelayedJob(cloudNetworkAgent, expectedJobStart, job.getJobId());
	}

	/**
	 * Method verifies if the job execution has started at the correct time.
	 * If there is some delay - it sends the request to the Server to provide information about the job start status
	 */
	@Override
	protected void onWake() {
		final ClientJob job = getJobById(jobId, myCloudNetworkAgent.getNetworkJobs());

		if (nonNull(job) && myCloudNetworkAgent.getServerForJobMap().containsKey(jobId)
				&& !myCloudNetworkAgent.getNetworkJobs().get(job).equals(IN_PROGRESS)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.error(JOB_DELAY_LOG);

			myAgent.addBehaviour(InitiateJobStartCheck.create(myCloudNetworkAgent, jobId, jobStart));
		}
	}
}
