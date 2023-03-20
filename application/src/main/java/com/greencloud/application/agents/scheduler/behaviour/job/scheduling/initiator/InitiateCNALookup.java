package com.greencloud.application.agents.scheduler.behaviour.job.scheduling.initiator;

import static com.greencloud.application.agents.scheduler.behaviour.job.scheduling.initiator.logs.JobSchedulingInitiatorLog.NO_CLOUD_AVAILABLE_NO_RETRY_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.job.scheduling.initiator.logs.JobSchedulingInitiatorLog.NO_CLOUD_AVAILABLE_RETRY_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.job.scheduling.initiator.logs.JobSchedulingInitiatorLog.NO_CLOUD_RESPONSES_LOG;
import static com.greencloud.application.agents.scheduler.behaviour.job.scheduling.initiator.logs.JobSchedulingInitiatorLog.SEND_ACCEPT_TO_CLOUD_LOG;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.FAILED_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SCHEDULER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.CallForProposalMessageFactory.createCallForProposal;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static com.greencloud.application.messages.domain.factory.JobStatusMessageFactory.preparePostponeJobMessageForClient;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.PROCESSING;
import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.greencloud.application.agents.scheduler.SchedulerAgent;
import com.greencloud.application.behaviours.initiator.AbstractCFPInitiator;
import com.greencloud.application.domain.job.JobWithPrice;
import com.greencloud.commons.domain.job.ClientJob;

import jade.lang.acl.ACLMessage;

/**
 * Behaviour looks for the Cloud Network that will handle the job execution
 */
public class InitiateCNALookup extends AbstractCFPInitiator<JobWithPrice> {

	private static final Logger logger = getLogger(InitiateCNALookup.class);
	private final SchedulerAgent myScheduler;
	private final ClientJob job;

	public InitiateCNALookup(final SchedulerAgent agent, final ACLMessage cfp, final ClientJob job) {
		super(agent, cfp, null, mapToJobInstanceId(job), agent.manage().offerComparator(), JobWithPrice.class);

		this.myScheduler = agent;
		this.job = job;
	}

	/**
	 * Method creates behaviour
	 *
	 * @param scheduler agent executing the behaviour
	 * @param job       job of interest
	 * @return InitiateCNALookup
	 */
	public static InitiateCNALookup create(final SchedulerAgent scheduler, final ClientJob job) {
		final ACLMessage cfp = createCallForProposal(job, scheduler.getAvailableCloudNetworks(),
				SCHEDULER_JOB_CFP_PROTOCOL);

		return new InitiateCNALookup(scheduler, cfp, job);
	}

	/**
	 * Method calls failure handler which verifies whether job can be postponed or if it fails
	 */
	@Override
	protected void handleNoResponses() {
		logger.info(NO_CLOUD_RESPONSES_LOG);
		handleFailure();
	}

	/**
	 * Method calls failure handler which verifies whether job can be postponed or if it fails
	 */
	@Override
	protected void handleNoAvailableAgents() {
		handleFailure();
	}

	/**
	 * Method responds with accept proposal to selected CNA and updates job status
	 */
	@Override
	protected void handleSelectedOffer(final JobWithPrice chosenOfferData) {
		logger.info(SEND_ACCEPT_TO_CLOUD_LOG, bestProposal.getSender().getName());

		myScheduler.getClientJobs().replace(job, PROCESSING, ACCEPTED);
		myScheduler.getCnaForJobMap().put(job.getJobId(), bestProposal.getSender());
		myScheduler.send(prepareReply(bestProposal, jobInstance, ACCEPT_PROPOSAL));
	}

	private void handleFailure() {
		if (myScheduler.manage().postponeJobExecution(job)) {
			logger.info(NO_CLOUD_AVAILABLE_RETRY_LOG);
			myScheduler.send(preparePostponeJobMessageForClient(job));
		} else {
			logger.info(NO_CLOUD_AVAILABLE_NO_RETRY_LOG);
			myScheduler.manage().jobFailureCleanUp(job);
			myScheduler.send(prepareJobStatusMessageForClient(job, FAILED_JOB_ID));
		}
	}
}
