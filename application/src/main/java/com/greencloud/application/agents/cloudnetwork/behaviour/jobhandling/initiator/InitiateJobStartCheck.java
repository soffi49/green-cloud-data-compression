package com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator;

import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.JOB_HAS_NOT_STARTED_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.initiator.logs.JobHandlingInitiatorLog.JOB_HAS_STARTED_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.DELAYED_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.STARTED_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.JOB_START_STATUS_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.prepareJobStatusMessageForScheduler;
import static com.greencloud.application.utils.JobUtils.getJobById;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobExecutionResultEnum.STARTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.IN_PROGRESS;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.domain.job.ImmutableJobStatusUpdate;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.message.MessageBuilder;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Behaviour retrieves the job start status from the Server Agent
 */
public class InitiateJobStartCheck extends AchieveREInitiator {

	private static final Logger logger = getLogger(InitiateJobStartCheck.class);

	private final CloudNetworkAgent myCloudNetwork;
	private final String jobId;
	private final Instant jobStart;

	protected InitiateJobStartCheck(final CloudNetworkAgent agent, final ACLMessage msg, final String jobId,
			final Instant jobStart) {
		super(agent, msg);
		this.myCloudNetwork = agent;
		this.jobId = jobId;
		this.jobStart = jobStart;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param cloudNetwork agent executing the behaviour
	 * @param jobId        unique identifier of the job of interest
	 * @param jobStart     time when the job execution is to be started
	 * @return InitiateJobStartCheck
	 */
	public static InitiateJobStartCheck create(final CloudNetworkAgent cloudNetwork, final String jobId,
			final Instant jobStart) {
		final AID server = cloudNetwork.getServerForJobMap().get(jobId);
		final ACLMessage request = MessageBuilder.builder()
				.withPerformative(REQUEST)
				.withStringContent(jobId)
				.withMessageProtocol(JOB_START_STATUS_PROTOCOL)
				.withReceivers(server)
				.build();

		return new InitiateJobStartCheck(cloudNetwork, request, jobId, jobStart);
	}

	/**
	 * Method handles the INFORM message confirming that the job execution has started.
	 * It sends the confirmation to the Scheduler
	 *
	 * @param inform server inform message
	 */
	@Override
	protected void handleInform(final ACLMessage inform) {
		final ClientJob job = getJobById(jobId, myCloudNetwork.getNetworkJobs());

		if (nonNull(job) && !myCloudNetwork.getNetworkJobs().get(job).equals(IN_PROGRESS)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.info(JOB_HAS_STARTED_LOG, jobId);

			final JobStatusUpdate jobStatusUpdate = new ImmutableJobStatusUpdate(mapToJobInstanceId(job), jobStart);
			myCloudNetwork.getNetworkJobs().replace(job, IN_PROGRESS);
			myCloudNetwork.manage().incrementJobCounter(mapToJobInstanceId(job), STARTED);
			myAgent.send(prepareJobStatusMessageForScheduler(myCloudNetwork, jobStatusUpdate, STARTED_JOB_ID));
		}
	}

	/**
	 * Method handles the REFUSE message informing that the job execution has not started.
	 * It sends the delay message to the Scheduler
	 *
	 * @param refuse refuse message
	 */
	@Override
	protected void handleRefuse(final ACLMessage refuse) {
		final ClientJob job = getJobById(jobId, myCloudNetwork.getNetworkJobs());

		if (nonNull(job) && !myCloudNetwork.getNetworkJobs().get(job).equals(IN_PROGRESS)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.error(JOB_HAS_NOT_STARTED_LOG, jobId);

			final JobStatusUpdate jobStatusUpdate = new ImmutableJobStatusUpdate(mapToJobInstanceId(job),
					getCurrentTime());
			myAgent.send(prepareJobStatusMessageForScheduler(myCloudNetwork, jobStatusUpdate, DELAYED_JOB_ID));
		}
	}
}
