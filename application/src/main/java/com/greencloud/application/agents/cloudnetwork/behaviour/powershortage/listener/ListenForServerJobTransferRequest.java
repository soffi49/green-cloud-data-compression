package com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener;

import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.logs.PowerShortageCloudListenerLog.SERVER_TRANSFER_REQUEST_ASK_SERVERS_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.logs.PowerShortageCloudListenerLog.SERVER_TRANSFER_REQUEST_JOB_NOT_FOUND_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.logs.PowerShortageCloudListenerLog.SERVER_TRANSFER_REQUEST_NO_SERVERS_AVAILABLE_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.templates.PowerShortageCloudMessageTemplates.SERVER_JOB_TRANSFER_REQUEST_TEMPLATE;
import static com.greencloud.application.agents.cloudnetwork.constants.CloudNetworkAgentConstants.MAX_MESSAGE_NUMBER_IN_BATCH;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_AGENT_NAME;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobNewStartTime;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.NO_SERVER_AVAILABLE_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getJobById;
import static com.greencloud.application.utils.TimeUtils.alignStartTimeToGivenTime;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.initiator.InitiateJobTransferRequest;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour receives the request from the Server that is/will be affected by the power shortage
 * and needs to perform the job transfer
 */
public class ListenForServerJobTransferRequest extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForServerJobTransferRequest.class);

	private CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Method casts the abstract agent to agent of type Cloud Network Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
	}

	/**
	 * Method listens for the messages coming from the Server requesting job transfer.
	 * It handles the information by announcing the job transfer request in network and looking
	 * for another server which may carry out the job execution.
	 */
	@Override
	public void action() {
		final List<ACLMessage> requests = myAgent.receive(SERVER_JOB_TRANSFER_REQUEST_TEMPLATE,
				MAX_MESSAGE_NUMBER_IN_BATCH);

		if (nonNull(requests)) {
			requests.stream().parallel().forEach(request -> {
				MDC.put(MDC_AGENT_NAME, myAgent.getLocalName());
				final JobPowerShortageTransfer transferData = readMessageContent(request,
						JobPowerShortageTransfer.class);
				final String jobId = transferData.getJobInstanceId().getJobId();
				final ClientJob job = getJobById(jobId, myCloudNetworkAgent.getNetworkJobs());

				MDC.put(MDC_JOB_ID, jobId);
				if (nonNull(job)) {
					final List<AID> remainingServers = myCloudNetworkAgent.manage()
							.getRemainingAgents(request.getSender(),
									myCloudNetworkAgent.manage().getOwnedActiveServers());
					final Instant shortageStartTime = transferData.getPowerShortageStart();

					if (!remainingServers.isEmpty()) {
						logger.info(SERVER_TRANSFER_REQUEST_ASK_SERVERS_LOG, job.getJobId());
						askRemainingServersToTransferJob(job, shortageStartTime, request, remainingServers);
					} else {
						logger.info(SERVER_TRANSFER_REQUEST_NO_SERVERS_AVAILABLE_LOG);
						myCloudNetworkAgent.send(
								prepareStringReply(request, NO_SERVER_AVAILABLE_CAUSE_MESSAGE, REFUSE));
					}
				} else {
					logger.info(SERVER_TRANSFER_REQUEST_JOB_NOT_FOUND_LOG, jobId);
					myCloudNetworkAgent.send(prepareStringReply(request, JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
				}
			});
		} else {
			block();
		}
	}

	private void askRemainingServersToTransferJob(final ClientJob originalJob, final Instant shortageStartTime,
			final ACLMessage request, final List<AID> remainingServers) {
		final Instant newJobStartTime = alignStartTimeToGivenTime(originalJob.getStartTime(), shortageStartTime);
		final ClientJob jobToTransfer = mapToJobNewStartTime(originalJob, newJobStartTime);

		myAgent.addBehaviour(InitiateJobTransferRequest.create(myCloudNetworkAgent, request, jobToTransfer,
				shortageStartTime, remainingServers));
	}
}
