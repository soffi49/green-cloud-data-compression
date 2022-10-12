package com.greencloud.application.agents.server.behaviour.powershortage.listener;

import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_CONFIRMED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_FAILED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_JOB_FINISHED_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.templates.PowerShortageServerMessageTemplates.SOURCE_JOB_TRANSFER_CONFIRMATION_TEMPLATE;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JsonMapper.getMapper;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.PowerShortageMessageContentConstants.TRANSFER_SUCCESSFUL_MESSAGE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static jade.lang.acl.MessageTemplate.and;

import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.powershortage.handler.HandleSourceJobTransfer;
import com.greencloud.application.domain.job.JobInstanceIdentifier;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour listens for the message from Green Source which confirms the power transfer
 */
public class ListenForSourceJobTransferConfirmation extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForSourceJobTransferConfirmation.class);

	private final MessageTemplate messageTemplate;
	private final ServerAgent myServerAgent;
	private final JobInstanceIdentifier jobToTransfer;
	private final ACLMessage greenSourceRequest;
	private final Instant powerShortageStart;

	/**
	 * Behaviours constructor
	 *
	 * @param agent              server executing the behaviour
	 * @param jobInstanceId      unique job instance identifier
	 * @param powerShortageTime  time when the power shortage starts
	 * @param greenSourceRequest original green source job transfer request
	 */
	public ListenForSourceJobTransferConfirmation(ServerAgent agent,
			JobInstanceIdentifier jobInstanceId,
			Instant powerShortageTime,
			ACLMessage greenSourceRequest) {
		super(agent);
		this.myServerAgent = agent;
		this.greenSourceRequest = greenSourceRequest;
		this.jobToTransfer = jobInstanceId;
		this.powerShortageStart = powerShortageTime;
		this.messageTemplate = createListenerTemplate(jobInstanceId);
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
	public void action() {
		final ACLMessage msg = myAgent.receive(messageTemplate);

		if (Objects.nonNull(msg)) {
			final String jobId = jobToTransfer.getJobId();
			if (msg.getPerformative() == INFORM) {
				MDC.put(MDC_JOB_ID, jobId);
				if (Objects.nonNull(myServerAgent.manage().getJobById(jobId))) {
					logger.info(GS_TRANSFER_CONFIRMED_LOG, jobId);
					handleJobTransfer(msg);
				} else {
					logger.info(GS_TRANSFER_JOB_FINISHED_LOG);
					handleJobFinish(msg.getSender());
				}
			} else {
				MDC.put(MDC_JOB_ID, jobId);
				logger.info(GS_TRANSFER_FAILED_LOG, jobId);
				myServerAgent.manage()
						.passTransferRequestToCloudNetwork(jobToTransfer, powerShortageStart, greenSourceRequest);
			}
		} else {
			block();
		}
	}

	private void handleJobTransfer(final ACLMessage inform) {
		final AID greenSourceSender = greenSourceRequest.getSender();
		displayMessageArrow(myServerAgent, greenSourceSender);
		myServerAgent.send(prepareReply(greenSourceRequest.createReply(), TRANSFER_SUCCESSFUL_MESSAGE, INFORM));
		myAgent.addBehaviour(HandleSourceJobTransfer.createFor(myServerAgent, jobToTransfer, inform.getSender()));
	}

	private void handleJobFinish(final AID responseSender) {
		final ACLMessage failTransferMessage = prepareReply(greenSourceRequest.createReply(),
				JOB_NOT_FOUND_CAUSE_MESSAGE, FAILURE);
		displayMessageArrow(myServerAgent, responseSender);
		myServerAgent.send(failTransferMessage);
	}
}
