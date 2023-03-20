package com.greencloud.application.agents.server.behaviour.jobexecution.initiator;

import static com.greencloud.application.agents.server.behaviour.jobexecution.initiator.logs.JobHandlingInitiatorLog.CANCEL_JOB_ALL_GS_RESPONSES;
import static com.greencloud.application.agents.server.behaviour.jobexecution.initiator.logs.JobHandlingInitiatorLog.CANCEL_JOB_IN_SERVER;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.prepareJobCancellationMessage;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.behaviours.initiator.AbstractCancelInitiator;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour initiates job cancellation in owned Green Sources
 */
public class InitiateJobCancellationInGreenSource extends AbstractCancelInitiator<ClientJob> {

	private static final Logger logger = getLogger(InitiateJobCancellationInGreenSource.class);

	private final ServerAgent myServer;

	public InitiateJobCancellationInGreenSource(final ServerAgent agent, final ACLMessage cancel,
			final List<ClientJob> jobsToCancel, final ACLMessage originalRequest) {
		super(agent, cancel, jobsToCancel, originalRequest);

		this.myServer = agent;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param agent         agent executing the behaviour
	 * @param originalJobId identifier of job before split
	 * @param jobsToCancel  list of jobs that are to be cancelled
	 * @return InitiateJobCancellationInGreenSource
	 */
	public static InitiateJobCancellationInGreenSource create(final ServerAgent agent, final String originalJobId,
			final List<ClientJob> jobsToCancel, final ACLMessage originalRequest) {
		final ACLMessage cancel = prepareJobCancellationMessage(originalJobId,
				agent.getOwnedGreenSources().keySet().toArray(new AID[0]));
		return new InitiateJobCancellationInGreenSource(agent, cancel, new ArrayList<>(jobsToCancel), originalRequest);
	}

	/**
	 * Method logs message after receiving all responses
	 */
	@Override
	public void postCancellation() {
		logger.info(CANCEL_JOB_ALL_GS_RESPONSES, jobsToCancel.size());
	}

	/**
	 * Method removes job from the Server jobs list and updates the internal state
	 *
	 * @param job job that is to be cancelled
	 */
	@Override
	public void handleJobCanceling(final ClientJob job) {
		MDC.put(MDC_JOB_ID, job.getJobId());
		logger.info(CANCEL_JOB_IN_SERVER, job.getJobId());

		if (isJobStarted(job, myServer.getServerJobs())) {
			myServer.manage().incrementJobCounter(mapToJobInstanceId(job), FINISH);
		}
		myServer.manage().finishJobExecutionWithResult(job, false, FAILED);
		myServer.manage().updateGUI();
	}
}
