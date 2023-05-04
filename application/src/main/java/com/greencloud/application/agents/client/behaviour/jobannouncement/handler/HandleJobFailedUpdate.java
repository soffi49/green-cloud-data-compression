package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.handler.logs.JobAnnouncementHandlerLog.PART_FAILED_LOG;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.utils.MessagingUtils.readMessageContent;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.domain.job.enums.JobClientStatusEnum;
import com.gui.agents.ClientAgentNode;

import jade.lang.acl.ACLMessage;

/**
 * Behaviour handles information regarding job failure
 */
public class HandleJobFailedUpdate extends AbstractJobUpdateHandler {

	private static final Logger logger = getLogger(HandleJobFailedUpdate.class);

	public HandleJobFailedUpdate(final ACLMessage message, final ClientAgent myClient,
			final ClientJobUpdateEnum updateEnum) {
		super(message, myClient, updateEnum);
	}

	/**
	 * Method updates the job status on GUI, logs failure event to the database and terminates the agent.
	 */
	@Override
	public void action() {
		final JobClientStatusEnum jobStatus = updateEnum.getJobStatus();
		final JobStatusUpdate jobUpdate = readMessageContent(message, JobStatusUpdate.class);

		if (myClient.isSplit()) {
			MDC.put(MDC_JOB_ID, myClient.getJobExecution().getJob().getJobId());
			logger.info(PART_FAILED_LOG);
		}

		((ClientAgentNode) myClient.getAgentNode()).updateJobStatus(jobStatus);
		myClient.getJobExecution().updateJobStatusDuration(jobStatus, jobUpdate.getChangeTime());
		myClient.getGuiController().updateFailedJobsCountByValue(1);
		myClient.manage().writeClientData(true);
		myClient.doDelete();
	}
}
