package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.utils.TimeUtils.convertToRealTime;
import static java.util.Objects.isNull;

import java.time.Instant;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.domain.ClientJobExecution;
import com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.domain.job.enums.JobClientStatusEnum;
import com.gui.agents.ClientAgentNode;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Abstract class extended by all handlers that process the information about job status update
 */
public abstract class AbstractJobUpdateHandler extends OneShotBehaviour {

	protected final ACLMessage message;
	protected final ClientAgent myClient;
	protected final ClientJobUpdateEnum updateEnum;

	/**
	 * Behaviour constructor
	 *
	 * @param message    message with job status update
	 * @param myClient   client executing the behaviour
	 * @param updateEnum type of the update message
	 */
	protected AbstractJobUpdateHandler(final ACLMessage message, final ClientAgent myClient,
			final ClientJobUpdateEnum updateEnum) {
		this.myClient = myClient;
		this.message = message;
		this.updateEnum = updateEnum;
	}

	@Override
	public int onEnd() {
		myClient.manage().writeClientData(false);
		return 0;
	}

	/**
	 * Method updates information after job status update (for original job, not split part)
	 *
	 * @param jobUpdate data of job update
	 */
	protected void updateInformationOfJobStatusUpdate(final JobStatusUpdate jobUpdate) {
		((ClientAgentNode) myClient.getAgentNode()).updateJobStatus(updateEnum.getJobStatus());
		myClient.getJobExecution().updateJobStatusDuration(updateEnum.getJobStatus(), jobUpdate.getChangeTime());
	}

	/**
	 * Method updates information after job part status update
	 *
	 * @param jobUpdate data of job part update
	 */
	protected void updateInformationOfJobPartStatusUpdate(final JobStatusUpdate jobUpdate) {
		final String jobPartId = jobUpdate.getJobInstance().getJobId();
		final JobClientStatusEnum jobStatus = updateEnum.getJobStatus();

		((ClientAgentNode) myClient.getAgentNode()).updateJobStatus(jobStatus, jobPartId);
		myClient.getJobParts().get(jobPartId).updateJobStatusDuration(jobStatus, jobUpdate.getChangeTime());
		myClient.manage().updateOriginalJobStatus(jobStatus);
	}

	/**
	 * Method updates time frames of client job (for original job, not job part)
	 *
	 * @param newStart new job start time
	 * @param newEnd   new job end time
	 */
	protected void readjustJobTimeFrames(final Instant newStart, final Instant newEnd) {
		myClient.getJobExecution().setJobSimulatedStart(newStart);
		myClient.getJobExecution().setJobSimulatedEnd(newEnd);

		((ClientAgentNode) myClient.getAgentNode()).updateJobTimeFrame(
				convertToRealTime(myClient.getJobExecution().getJobSimulatedStart()),
				convertToRealTime(myClient.getJobExecution().getJobSimulatedEnd()));
	}

	/**
	 * Method updates time frames of client job part
	 *
	 * @param jobPartId id of given job part
	 * @param newStart  new job start time
	 * @param newEnd    new job end time
	 */
	protected void readjustJobPartTimeFrames(final String jobPartId, final Instant newStart, final Instant newEnd) {
		final ClientJobExecution jobPart = myClient.getJobParts().get(jobPartId);

		jobPart.setJobSimulatedStart(newStart);
		jobPart.setJobSimulatedEnd(newEnd);

		((ClientAgentNode) myClient.getAgentNode()).updateJobTimeFrame(
				convertToRealTime(jobPart.getJobSimulatedStart()),
				convertToRealTime(jobPart.getJobSimulatedEnd()),
				jobPartId);
	}
}
