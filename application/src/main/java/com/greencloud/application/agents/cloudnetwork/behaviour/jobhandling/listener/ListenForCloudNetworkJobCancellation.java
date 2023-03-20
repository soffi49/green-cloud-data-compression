package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener;

import static com.google.common.collect.Collections2.filter;
import static com.greencloud.application.agents.scheduler.behaviour.job.cancellation.logs.JobCancellationLogs.CANCELLED_JOB_PART_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.job.cancellation.logs.JobCancellationLogs.CANCELLING_JOB_PARTS_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.job.cancellation.templates.JobCancellationMessageTemplates.CANCEL_JOB_ANNOUNCEMENT;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.PRE_EXECUTION;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for job cancellation announcements.
 * If any part of the job is processed by the agent, it is removed from processing, otherwise refusal message is sent.
 */
//TODO revise job cancellation process
public class ListenForCloudNetworkJobCancellation extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForCloudNetworkJobCancellation.class);

	private CloudNetworkAgent myCloudNetworkAgent;

	@Override
	public void action() {
		var message = myCloudNetworkAgent.receive(CANCEL_JOB_ANNOUNCEMENT);
		if (nonNull(message)) {
			processJobCancellation(message.getContent(), message);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	private void processJobCancellation(String originalJobId, ACLMessage message) {
		var jobParts = List.copyOf(filter(myCloudNetworkAgent.getNetworkJobs().keySet(),
				job -> job.getJobId().split("#")[0].equals(originalJobId)));
		if (!jobParts.isEmpty()) {
			MDC.put(MDC_JOB_ID, originalJobId);
			logger.info(CANCELLING_JOB_PARTS_LOG, jobParts.size());
			jobParts.forEach(jobPart -> {
				var jobPartStatus = myCloudNetworkAgent.getNetworkJobs().get(jobPart);
				myCloudNetworkAgent.getNetworkJobs().remove(jobPart);
				myCloudNetworkAgent.getServerForJobMap().remove(jobPart.getJobId());
				if (!PRE_EXECUTION.getStatuses().contains(jobPartStatus)) {
					if (!jobPartStatus.equals(ACCEPTED)) {
						myCloudNetworkAgent.manage()
								.incrementJobCounter(mapToJobInstanceId(jobPart), JobExecutionResultEnum.FINISH);
					} else {
						myCloudNetworkAgent.getGuiController().updateAllJobsCountByValue(-1);
					}
				}
				MDC.put(MDC_JOB_ID, jobPart.getJobId());
				logger.info(CANCELLED_JOB_PART_LOG);
			});
			myCloudNetworkAgent.send(prepareReply(message, jobParts, INFORM));
		} else {
			myCloudNetworkAgent.send(prepareRefuseReply(message));
		}
	}
}
