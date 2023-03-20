package com.greencloud.application.agents.server.behaviour.powershortage.listener;

import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_CONFIRMED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_JOB_FINISHED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.templates.PowerShortageServerMessageTemplates.SOURCE_JOB_TRANSFER_CONFIRMATION_TEMPLATE;
import static com.greencloud.application.agents.server.constants.ServerAgentConstants.TRANSFER_EXPIRATION_TIME;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JsonMapper.getMapper;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.TRANSFER_SUCCESSFUL_MESSAGE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDate;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_GREEN;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ON_HOLD_TRANSFER;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static jade.lang.acl.MessageTemplate.and;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.powershortage.handler.HandleSourceJobTransfer;
import com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.commons.domain.job.ClientJob;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

/**
 * Behaviour listens for the message from Green Source which confirms the power transfer
 */
public class ListenForSourceJobTransferConfirmation extends MsgReceiver {

	private static final Logger logger = getLogger(ListenForSourceJobTransferConfirmation.class);

	private final ServerAgent myServerAgent;
	private final JobInstanceIdentifier jobToTransfer;
	private final ACLMessage greenSourceRequest;
	private final Instant shortageStart;

	/**
	 * Behaviours constructor
	 *
	 * @param agent              server executing the behaviour
	 * @param jobInstanceId      unique job instance identifier
	 * @param powerShortageTime  time when the power shortage starts
	 * @param greenSourceRequest original green source job transfer request
	 */
	public ListenForSourceJobTransferConfirmation(final ServerAgent agent, final JobInstanceIdentifier jobInstanceId,
			final Instant powerShortageTime, final ACLMessage greenSourceRequest) {
		super(agent, createListenerTemplate(jobInstanceId), currentTimeMillis() + TRANSFER_EXPIRATION_TIME, null, null);

		this.myServerAgent = agent;
		this.greenSourceRequest = greenSourceRequest;
		this.jobToTransfer = jobInstanceId;
		this.shortageStart = powerShortageTime;
	}

	private static MessageTemplate createListenerTemplate(final JobInstanceIdentifier jobInstanceId) {
		try {
			final String expectedContent = getMapper().writeValueAsString(jobInstanceId);
			return and(MatchContent(expectedContent), SOURCE_JOB_TRANSFER_CONFIRMATION_TEMPLATE);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method listens for the confirmation message coming from Green Energy Source.
	 * It schedules the transfer execution and sends the response to Green Source which requested the transfer.
	 */
	@Override
	public void handleMessage(final ACLMessage msg) {
		if (nonNull(msg)) {
			final String jobId = jobToTransfer.getJobId();

			MDC.put(MDC_JOB_ID, jobId);
			switch (msg.getPerformative()) {
				case INFORM -> handleJobTransfer(msg);
				default -> handleTransferFailure(jobId);
			}
		} else {
			block();
		}
	}

	private void handleJobTransfer(final ACLMessage inform) {
		final ClientJob job = getJobByIdAndStartDate(jobToTransfer, myServerAgent.getServerJobs());

		if (nonNull(job)) {
			logger.info(GS_TRANSFER_CONFIRMED_LOG, job.getJobId());
			myServerAgent.send(prepareReply(greenSourceRequest, TRANSFER_SUCCESSFUL_MESSAGE, INFORM));
			updateJobStatus(job);
			myAgent.addBehaviour(HandleSourceJobTransfer.create(myServerAgent, jobToTransfer, inform.getSender()));
		} else {
			logger.info(GS_TRANSFER_JOB_FINISHED_LOG);
			myServerAgent.send(prepareStringReply(greenSourceRequest, JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
		}
	}

	private void handleTransferFailure(final String jobId) {
		logger.info(PowerShortageServerListenerLog.GS_TRANSFER_FAILED_LOG, jobId);
		myServerAgent.message().passTransferRequestToCloudNetwork(jobToTransfer, shortageStart, greenSourceRequest);
	}

	private void updateJobStatus(final ClientJob jobToExecute) {
		final boolean isJobRunning = myServerAgent.getServerJobs().get(jobToExecute).equals(ON_HOLD_TRANSFER);
		myServerAgent.getServerJobs().replace(jobToExecute, EXECUTING_ON_GREEN.getStatus(isJobRunning));
	}
}
