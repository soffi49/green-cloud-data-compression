package com.greencloud.application.agents.greenenergy.behaviour.cancellation;

import static com.google.common.collect.Collections2.filter;
import static com.greencloud.application.agents.scheduler.behaviour.job.cancellation.templates.JobCancellationMessageTemplates.CANCEL_JOB_ANNOUNCEMENT;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_PLANNED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;
import static jade.lang.acl.ACLMessage.AGREE;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.util.List.of;
import static java.util.Objects.nonNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.scheduler.behaviour.job.cancellation.logs.JobCancellationLogs;
import com.greencloud.application.common.constant.LoggingConstant;
import com.greencloud.application.mapper.JobMapper;
import com.greencloud.commons.domain.job.ServerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Listens for job cancellation announcements. If any part of the job is processed by the
 * agent, it is removed from processing, otherwise refusal message is sent.
 */
//TODO DEFINITELY SHOULD BE REFACTORED!!!!!
public class ListenForGreenEnergyJobCancellation extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForGreenEnergyJobCancellation.class);

	private static final List<JobExecutionStatusEnum> JOB_NOT_STARTED_STATUSES =
			of(CREATED, PROCESSING, ACCEPTED, ON_HOLD_PLANNED);

	private GreenEnergyAgent myGreenEnergyAgent;

	@Override
	public void action() {
		var message = myGreenEnergyAgent.receive(CANCEL_JOB_ANNOUNCEMENT);

		if (nonNull(message)) {
			processJobCancellation(message.getContent(), message);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
	}

	private void processJobCancellation(String originalJobId, ACLMessage message) {
		var jobParts = List.copyOf((filter(myGreenEnergyAgent.getServerJobs().keySet(),
				job -> job.getJobId().split("#")[0].equals(originalJobId))));
		if (!jobParts.isEmpty()) {
			myGreenEnergyAgent.send(prepareStringReply(message, originalJobId, AGREE));
			MDC.put(LoggingConstant.MDC_JOB_ID, originalJobId);
			logger.info(JobCancellationLogs.CANCELLING_JOB_PARTS_LOG, jobParts.size());
			jobParts.forEach(this::processJobPart);
			var powerJobParts = jobParts.stream().map(JobMapper::mapServerJobToPowerJob).toList();
			myGreenEnergyAgent.send(prepareReply(message, powerJobParts, INFORM));
		} else {
			myGreenEnergyAgent.send(prepareRefuseReply(message));
		}
	}

	private void processJobPart(ServerJob jobPart) {
		var jobPartStatus = myGreenEnergyAgent.getServerJobs().get(jobPart);
		myGreenEnergyAgent.manage().removeJob(jobPart);
		if (!JOB_NOT_STARTED_STATUSES.contains(jobPartStatus)) {
			myGreenEnergyAgent.manage().incrementJobCounter(mapToJobInstanceId(jobPart), FINISH);
		}
		myGreenEnergyAgent.manage().incrementJobCounter(mapToJobInstanceId(jobPart), FAILED);
		MDC.put(LoggingConstant.MDC_JOB_ID, jobPart.getJobId());
		logger.info(JobCancellationLogs.CANCELLED_JOB_PART_LOG);
		myGreenEnergyAgent.manage().updateGUI();
	}
}
