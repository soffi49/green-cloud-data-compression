package com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener;

import static com.google.common.collect.Collections2.filter;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.CANCEL_JOB_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.template.PowerSupplyMessageTemplates.CANCEL_JOB_ANNOUNCEMENT_GREEN_SOURCE_TEMPLATE;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.utils.JobUtils.getJobName;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FAILED;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.FINISH;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_PLANNED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.util.List.of;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.commons.domain.job.ServerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for job cancellation announcements. If any part of the job is processed by the
 * agent, it is removed from processing, otherwise refusal message is sent.
 */
public class ListenForGreenEnergyJobCancellation extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForGreenEnergyJobCancellation.class);
	private static final List<JobExecutionStatusEnum> JOB_NOT_STARTED_STATUSES =
			of(CREATED, PROCESSING, ACCEPTED, ON_HOLD_PLANNED);

	private GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Method casts the abstract agent to agent of type Green Energy Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
	}

	/**
	 * Method listens for job cancellation information. If any job parts are to be canceled then they are removed
	 * from jobs list and the Green Source informs about that the parent Server.
	 */
	@Override
	public void action() {
		final ACLMessage message = myGreenEnergyAgent.receive(CANCEL_JOB_ANNOUNCEMENT_GREEN_SOURCE_TEMPLATE);

		if (nonNull(message)) {
			final String jobId = message.getContent();
			final List<ServerJob> jobParts = getJobParts(jobId);

			if (!jobParts.isEmpty()) {
				MDC.put(MDC_JOB_ID, jobId);
				logger.info(CANCEL_JOB_LOG, jobParts.size());
				jobParts.forEach(this::processJobPart);

				final List<String> cancelledParts = jobParts.stream().map(ServerJob::getJobId).toList();
				myGreenEnergyAgent.send(prepareReply(message, cancelledParts, INFORM));
			} else {
				myGreenEnergyAgent.send(prepareRefuseReply(message));
			}
		}
	}

	private void processJobPart(ServerJob jobPart) {
		final JobExecutionStatusEnum jobPartStatus = myGreenEnergyAgent.getServerJobs().get(jobPart);

		myGreenEnergyAgent.manage().removeJob(jobPart);
		if (!JOB_NOT_STARTED_STATUSES.contains(jobPartStatus)) {
			myGreenEnergyAgent.manage().incrementJobCounter(mapToJobInstanceId(jobPart), FINISH);
		}
		myGreenEnergyAgent.manage().incrementJobCounter(mapToJobInstanceId(jobPart), FAILED);
		myGreenEnergyAgent.manage().updateGUI();
	}

	private List<ServerJob> getJobParts(final String jobId) {
		return List.copyOf(filter(myGreenEnergyAgent.getServerJobs().keySet(), job -> getJobName(job).equals(jobId)));
	}
}
