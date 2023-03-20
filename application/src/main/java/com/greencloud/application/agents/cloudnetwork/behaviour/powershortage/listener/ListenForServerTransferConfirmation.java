package com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener;

import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.logs.PowerShortageCloudListenerLog.SERVER_TRANSFER_CONFIRMED_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.logs.PowerShortageCloudListenerLog.SERVER_TRANSFER_FAILED_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.templates.PowerShortageCloudMessageTemplates.SERVER_JOB_TRANSFER_CONFIRMATION_TEMPLATE;
import static com.greencloud.application.agents.cloudnetwork.constants.CloudNetworkAgentConstants.TRANSFER_EXPIRATION_TIME;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.mapper.JsonMapper.getMapper;
import static com.greencloud.application.messages.constants.MessageContentConstants.SERVER_INTERNAL_FAILURE_CAUSE_MESSAGE;
import static com.greencloud.application.messages.constants.MessageContentConstants.TRANSFER_SUCCESSFUL_MESSAGE;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.TimeUtils.alignStartTimeToGivenTime;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static jade.lang.acl.MessageTemplate.and;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.handler.HandleJobTransferToServer;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

/**
 * Behaviour receives the messages confirming that the job transfer was accepted in given Server
 */
public class ListenForServerTransferConfirmation extends MsgReceiver {
	private static final Logger logger = getLogger(ListenForServerTransferConfirmation.class);

	private final CloudNetworkAgent myCloudNetworkAgent;
	private final ACLMessage serverMessage;
	private final JobPowerShortageTransfer powerShortageJob;
	private final AID server;

	private ListenForServerTransferConfirmation(final CloudNetworkAgent agent, final MessageTemplate template,
			final ACLMessage serverMessage, final JobPowerShortageTransfer powerShortageJob, final AID server) {
		super(agent, template, TRANSFER_EXPIRATION_TIME + currentTimeMillis(), null, null);

		this.serverMessage = serverMessage;
		this.myCloudNetworkAgent = agent;
		this.powerShortageJob = powerShortageJob;
		this.server = server;
	}

	/**
	 * Method creates the behaviour.
	 *
	 * @param agent            agent executing the behaviour
	 * @param serverMessage    transfer request message received from Server
	 * @param powerShortageJob job that is being transferred
	 * @param server           server to which the job is transferred
	 */
	public static ListenForServerTransferConfirmation create(final CloudNetworkAgent agent,
			final ACLMessage serverMessage, final JobPowerShortageTransfer powerShortageJob, final AID server) {
		final MessageTemplate template = and(SERVER_JOB_TRANSFER_CONFIRMATION_TEMPLATE,
				MatchContent(getExpectedContent(powerShortageJob)));
		return new ListenForServerTransferConfirmation(agent, template, serverMessage, powerShortageJob, server);
	}

	private static String getExpectedContent(final JobPowerShortageTransfer powerShortageJob) {
		try {
			final Instant startTime = alignStartTimeToGivenTime(powerShortageJob.getPowerShortageStart(),
					powerShortageJob.getJobInstanceId().getStartTime());
			return getMapper().writeValueAsString(mapToJobInstanceId(powerShortageJob.getJobInstanceId(), startTime));
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	/**
	 * Method listens for the messages coming from the Server confirming that the job transfer
	 * will be executed by the Server
	 */
	@Override
	protected void handleMessage(ACLMessage msg) {
		if (nonNull(msg)) {
			final String jobId = powerShortageJob.getJobInstanceId().getJobId();

			MDC.put(MDC_JOB_ID, jobId);
			if (msg.getPerformative() == INFORM) {
				logger.info(SERVER_TRANSFER_CONFIRMED_LOG, jobId);
				myCloudNetworkAgent.send(prepareStringReply(serverMessage, TRANSFER_SUCCESSFUL_MESSAGE, INFORM));
				myCloudNetworkAgent.addBehaviour(
						HandleJobTransferToServer.createFor(myCloudNetworkAgent, powerShortageJob, server));
			} else {
				logger.info(SERVER_TRANSFER_FAILED_LOG, jobId);
				myCloudNetworkAgent.send(
						prepareStringReply(serverMessage, SERVER_INTERNAL_FAILURE_CAUSE_MESSAGE, FAILURE));
			}
		}
	}

}
