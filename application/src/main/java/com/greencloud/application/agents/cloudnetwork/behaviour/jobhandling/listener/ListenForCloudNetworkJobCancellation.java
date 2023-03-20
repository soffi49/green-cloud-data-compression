package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener;

import static com.google.common.collect.Collections2.filter;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.logs.JobHandlingListenerLog.CANCEL_JOB_IN_SERVERS;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.templates.JobHandlingMessageTemplates.CANCEL_JOB_ANNOUNCEMENT_CNA_TEMPLATE;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.utils.JobUtils.getJobName;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.InitiateJobCancelInServers;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for job cancellation messages and communicates with owned Servers in order to cancel job execution
 */
public class ListenForCloudNetworkJobCancellation extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForCloudNetworkJobCancellation.class);

	private CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Method casts the abstract agent to the agent of type CloudNetworkAgent
	 */
	@Override
	public void onStart() {
		super.onStart();
		myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * Method listens for job cancellation information. It evaluates if any job parts are to be cancelled and if so
	 * forwards the request to corresponding Servers.
	 */
	@Override
	public void action() {
		final ACLMessage message = myCloudNetworkAgent.receive(CANCEL_JOB_ANNOUNCEMENT_CNA_TEMPLATE);

		if (nonNull(message)) {
			final String jobId = message.getContent();
			final List<ClientJob> jobParts = getJobParts(jobId);

			if (!jobParts.isEmpty()) {
				MDC.put(MDC_JOB_ID, jobId);
				logger.info(CANCEL_JOB_IN_SERVERS, jobParts.size(), jobId);
				myCloudNetworkAgent.addBehaviour(
						InitiateJobCancelInServers.create(myCloudNetworkAgent, jobId, jobParts, message));
			} else {
				myCloudNetworkAgent.send(prepareRefuseReply(message));
			}
		}
	}

	private List<ClientJob> getJobParts(final String jobId) {
		return List.copyOf(filter(myCloudNetworkAgent.getNetworkJobs().keySet(), job -> getJobName(job).equals(jobId)));
	}
}
