package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener;

import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.templates.JobHandlingMessageTemplates.JOB_STATUS_CHANGE_TEMPLATE;
import static com.greencloud.application.agents.cloudnetwork.constants.CloudNetworkAgentConstants.MAX_MESSAGE_NUMBER_IN_BATCH;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.FAILED_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.FAILED_JOB_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.prepareJobStatusMessageForScheduler;
import static com.greencloud.application.utils.JobUtils.getJobById;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.domain.CloudNetworkJobUpdateEnum;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles the message received from Server that informs about updates regarding job execution
 */
public class ListenForJobStatusChange extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForJobStatusChange.class);

	private CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Method casts the abstract agent to the agent of type CloudNetworkAgent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * Method listens for the information regarding new job status.
	 * It selects appropriate handler based on the message type and then logs associated with it information,
	 * executes the processing method and finally, if indicated, forwards the update to the Scheduler.
	 */
	@Override
	public void action() {
		final List<ACLMessage> messages = myAgent.receive(JOB_STATUS_CHANGE_TEMPLATE, MAX_MESSAGE_NUMBER_IN_BATCH);

		if (nonNull(messages)) {
			messages.forEach(message -> {
				final JobStatusUpdate jobStatusUpdate = readMessageContent(message, JobStatusUpdate.class);
				final String messageType =
						message.getProtocol().equals(FAILED_JOB_PROTOCOL) ? FAILED_JOB_ID : message.getConversationId();

				handleJobUpdate(jobStatusUpdate, messageType);
			});
		} else {
			block();
		}
	}

	private void handleJobUpdate(final JobStatusUpdate jobStatusUpdate, final String type) {
		final String jobId = jobStatusUpdate.getJobInstance().getJobId();
		final ClientJob job = getJobById(jobId, myCloudNetworkAgent.getNetworkJobs());

		if (nonNull(job)) {
			final CloudNetworkJobUpdateEnum jobUpdateHandler = CloudNetworkJobUpdateEnum.valueOf(type);

			MDC.put(MDC_JOB_ID, jobId);
			logger.info(jobUpdateHandler.getLogMessage(), jobId);

			if (nonNull(jobUpdateHandler.getJobUpdateHandler())) {
				jobUpdateHandler.getJobUpdateHandler().accept(job, myCloudNetworkAgent);
			}
			if (jobUpdateHandler.isInformScheduler()) {
				myAgent.send(prepareJobStatusMessageForScheduler(myCloudNetworkAgent, jobStatusUpdate, type));
			}
		}
	}
}
