package com.greencloud.application.agents.server.behaviour.jobexecution.listener;

import static com.google.common.collect.Collections2.filter;
import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog.CANCEL_JOB_IN_GREEN_SOURCES;
import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.templates.JobHandlingMessageTemplates.CANCEL_JOB_ANNOUNCEMENT_SERVER_TEMPLATE;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.utils.JobUtils.getJobName;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.jobexecution.initiator.InitiateJobCancellationInGreenSource;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for job cancellation announcements.
 * If any part of the job is processed by the agent, it is removed from processing, otherwise refusal message is sent.
 */
public class ListenForServerJobCancellation extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForServerJobCancellation.class);

	private ServerAgent myServerAgent;

	/**
	 * Method casts the abstract agent to agent of type Server Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method listens for job cancellation information. It evaluates if any job parts are to be cancelled and if so
	 * forwards the request to corresponding Green Sources.
	 */
	@Override
	public void action() {
		final ACLMessage message = myServerAgent.receive(CANCEL_JOB_ANNOUNCEMENT_SERVER_TEMPLATE);

		if (nonNull(message)) {
			final String jobId = message.getContent();
			final List<ClientJob> jobParts = getJobParts(jobId);

			if (!jobParts.isEmpty()) {
				MDC.put(MDC_JOB_ID, jobId);
				logger.info(CANCEL_JOB_IN_GREEN_SOURCES, jobParts.size(), jobId);
				myServerAgent.addBehaviour(
						InitiateJobCancellationInGreenSource.create(myServerAgent, jobId, jobParts, message));
			} else {
				myServerAgent.send(prepareRefuseReply(message));
			}
		}
	}

	private List<ClientJob> getJobParts(final String jobId) {
		return List.copyOf(filter(myServerAgent.getServerJobs().keySet(), job -> getJobName(job).equals(jobId)));
	}
}
