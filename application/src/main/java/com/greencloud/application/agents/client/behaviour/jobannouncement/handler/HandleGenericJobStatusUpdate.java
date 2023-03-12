package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.messages.MessagingUtils.readMessageContent;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum;
import com.greencloud.application.domain.job.JobStatusUpdate;

import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles in generic way information about job status update
 */
public class HandleGenericJobStatusUpdate extends AbstractJobUpdateHandler {

	public HandleGenericJobStatusUpdate(final ACLMessage message, final ClientAgent myClient,
			final ClientJobUpdateEnum updateEnum) {
		super(message, myClient, updateEnum);
	}

	/**
	 * Method updates information about job status and duration of job execution at the previous status
	 */
	@Override
	public void action() {
		final JobStatusUpdate jobUpdate = readMessageContent(message, JobStatusUpdate.class);

		if (!myClient.isSplit()) {
			updateInformationOfJobStatusUpdate(jobUpdate);
		} else  {
			updateInformationOfJobPartStatusUpdate(jobUpdate);
		}
	}
}
