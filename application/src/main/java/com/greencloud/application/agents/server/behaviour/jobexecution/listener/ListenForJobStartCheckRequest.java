package com.greencloud.application.agents.server.behaviour.jobexecution.listener;

import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog.JOB_START_STATUS_RECEIVED_REQUEST_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.templates.JobHandlingMessageTemplates.JOB_STATUS_REQUEST_TEMPLATE;
import static com.greencloud.application.agents.server.constants.ServerAgentConstants.MAX_MESSAGE_NUMBER;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getCurrentJobInstance;
import static com.greencloud.application.utils.JobUtils.isJobStarted;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles request coming from Cloud Network Agent asking for the job start status
 */
public class ListenForJobStartCheckRequest extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForJobStartCheckRequest.class);

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
	 * Method listens for the requests coming from Cloud Network and responds with either:
	 * <p> - INFORM when the job has started </p>
	 * <p> - REFUSE if it has not started yet </p>
	 **/
	@Override
	public void action() {
		final List<ACLMessage> requests = myAgent.receive(JOB_STATUS_REQUEST_TEMPLATE, MAX_MESSAGE_NUMBER);

		if (nonNull(requests)) {
			requests.forEach(request -> {
				final String jobId = request.getContent();
				MDC.put(MDC_JOB_ID, jobId);
				logger.info(JOB_START_STATUS_RECEIVED_REQUEST_LOG, jobId);

				final Map.Entry<ClientJob, JobExecutionStatusEnum> jobInstance =
						getCurrentJobInstance(jobId, myServerAgent.getServerJobs());
				myServerAgent.send(createReplyWithJobStatus(request, jobInstance));
			});
		} else {
			block();
		}
	}

	private ACLMessage createReplyWithJobStatus(final ACLMessage message,
			final Map.Entry<ClientJob, JobExecutionStatusEnum> jobInstance) {
		return nonNull(jobInstance) && isJobStarted(jobInstance.getValue()) ?
				prepareStringReply(message, "JOB STARTED", INFORM) :
				prepareStringReply(message, "JOB HAS NOT STARTED", REFUSE);
	}
}
