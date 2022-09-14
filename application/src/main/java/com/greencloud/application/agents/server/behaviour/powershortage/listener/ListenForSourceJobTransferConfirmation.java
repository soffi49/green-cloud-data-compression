package com.greencloud.application.agents.server.behaviour.powershortage.listener;

import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static jade.lang.acl.MessageTemplate.and;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.powershortage.handler.HandleSourceJobTransfer;
import com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog;
import com.greencloud.application.agents.server.behaviour.powershortage.listener.templates.PowerShortageServerMessageTemplates;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.mapper.JsonMapper;
import com.greencloud.application.messages.domain.factory.JobStatusMessageFactory;
import com.greencloud.application.messages.domain.factory.ReplyMessageFactory;

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
	private final String guid;
	private final JobInstanceIdentifier jobToTransfer;
	private final ACLMessage greenSourceRequest;

	/**
	 * Behaviours constructor
	 *
	 * @param agent              server executing the behaviour
	 * @param jobInstanceId      unique job instance identifier
	 * @param greenSourceRequest original green source job transfer request
	 */
	public ListenForSourceJobTransferConfirmation(ServerAgent agent,
			JobInstanceIdentifier jobInstanceId,
			ACLMessage greenSourceRequest) {
		super(agent);
		this.myServerAgent = agent;
		this.guid = agent.getName();
		this.greenSourceRequest = greenSourceRequest;
		this.jobToTransfer = jobInstanceId;
		this.messageTemplate = createListenerTemplate(jobInstanceId);
	}

	private static MessageTemplate createListenerTemplate(final JobInstanceIdentifier jobInstanceId) {
		try {
			final String expectedContent = JsonMapper.getMapper().writeValueAsString(jobInstanceId);
			return and(MatchContent(expectedContent), PowerShortageServerMessageTemplates.SOURCE_JOB_TRANSFER_CONFIRMATION_TEMPLATE);
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
		final ACLMessage inform = myAgent.receive(messageTemplate);

		if (Objects.nonNull(inform)) {
			final String jobId = jobToTransfer.getJobId();
			if (Objects.nonNull(myServerAgent.manage().getJobById(jobId))) {
				logger.info(PowerShortageServerListenerLog.GS_TRANSFER_CONFIRMED_LOG, guid, jobId);
				handleJobTransfer(inform);
			} else {
				logger.info(PowerShortageServerListenerLog.GS_TRANSFER_JOB_FINISHED_LOG, guid);
				handleJobFinish(jobId, inform.getSender());
			}
		} else {
			block();
		}
	}

	private void handleJobTransfer(final ACLMessage inform) {
		final AID greenSourceSender = greenSourceRequest.getSender();
		displayMessageArrow(myServerAgent, greenSourceSender);
		myServerAgent.send(ReplyMessageFactory.prepareReply(greenSourceRequest.createReply(), jobToTransfer, INFORM));
		myAgent.addBehaviour(HandleSourceJobTransfer.createFor(myServerAgent, jobToTransfer, inform.getSender()));
	}

	private void handleJobFinish(final String jobId, final AID responseSender) {
		final ACLMessage finishJobMessage = JobStatusMessageFactory.prepareFinishMessage(jobId, jobToTransfer.getStartTime(),
				List.of(responseSender));
		final ACLMessage failTransferMessage = ReplyMessageFactory.prepareReply(greenSourceRequest.createReply(), jobToTransfer, FAILURE);

		displayMessageArrow(myServerAgent, responseSender);
		myServerAgent.send(finishJobMessage);
		myServerAgent.send(failTransferMessage);
	}
}
