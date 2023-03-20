package com.greencloud.application.agents.server.behaviour.powershortage.listener;

import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_REQUEST_ASK_OTHER_GS_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_TRANSFER_REQUEST_NO_GS_AVAILABLE_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.templates.PowerShortageServerMessageTemplates.SOURCE_JOB_TRANSFER_REQUEST_TEMPLATE;
import static com.greencloud.application.agents.server.constants.ServerAgentConstants.MAX_MESSAGE_NUMBER;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToPowerJob;
import static com.greencloud.application.mapper.JobMapper.mapToPowerShortageJob;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.DELAYED_JOB_ALREADY_FINISHED_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDate;
import static com.greencloud.application.utils.TimeUtils.alignStartTimeToGivenTime;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import com.greencloud.application.agents.server.behaviour.powershortage.initiator.InitiateJobTransferInCloudNetwork;
import com.greencloud.application.agents.server.behaviour.powershortage.initiator.InitiateJobTransferInGreenSources;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.PowerJob;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the job transfer request coming from Green Source
 */
public class ListenForSourceJobTransferRequest extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForSourceJobTransferRequest.class);
	private ServerAgent myServerAgent;

	/**
	 * Method casts the abstract agent to agent of type Server Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method listens for the messages coming from the Green Source informing about power shortage and requesting the
	 * job transfer.
	 * It sends the CFP to remaining Green Sources asking for job transfer.
	 * If there are no available green sources, it passes the transfer request to the parent Cloud Network.
	 */
	@Override
	public void action() {
		final List<ACLMessage> messages = myAgent.receive(SOURCE_JOB_TRANSFER_REQUEST_TEMPLATE, MAX_MESSAGE_NUMBER);

		if (nonNull(messages)) {
			messages.stream().parallel().forEach(this::processTransferMessage);
		} else {
			block();
		}
	}

	private void processTransferMessage(final ACLMessage message) {
		final JobPowerShortageTransfer transfer = readMessageContent(message, JobPowerShortageTransfer.class);
		final ClientJob job = getJobByIdAndStartDate(transfer.getJobInstanceId(), myServerAgent.getServerJobs());

		if (nonNull(job)) {
			MDC.put(MDC_JOB_ID, job.getJobId());

			if (!job.getEndTime().isAfter(getCurrentTime())) {
				myAgent.send(prepareStringReply(message, DELAYED_JOB_ALREADY_FINISHED_CAUSE_MESSAGE, REFUSE));
			} else {
				schedulePowerShortageHandling(transfer, job);
				final PowerJob powerJob = createJobTransferInstance(transfer, job);
				final List<AID> greenSources = myServerAgent.manage().getRemainingAgents(message.getSender(),
						myServerAgent.manage().getOwnedActiveGreenSources());

				if (!greenSources.isEmpty()) {
					logger.info(GS_TRANSFER_REQUEST_ASK_OTHER_GS_LOG, powerJob.getJobId());
					myAgent.addBehaviour(InitiateJobTransferInGreenSources.create(myServerAgent, powerJob,
							greenSources, message, transfer.getPowerShortageStart()));
				} else {
					logger.info(GS_TRANSFER_REQUEST_NO_GS_AVAILABLE_LOG);
					passTransferRequestToCNA(transfer, powerJob, message);
				}
			}
		} else {
			myAgent.send(prepareStringReply(message, JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
		}
	}

	private PowerJob createJobTransferInstance(final JobPowerShortageTransfer transfer, final ClientJob originalJob) {
		final Instant start = alignStartTimeToGivenTime(transfer.getPowerShortageStart(), originalJob.getStartTime());
		return mapToPowerJob(originalJob, start);
	}

	private void passTransferRequestToCNA(final JobPowerShortageTransfer affectedJob, final PowerJob job,
			final ACLMessage transferRequest) {
		final JobPowerShortageTransfer jobToTransfer = mapToPowerShortageJob(job, affectedJob.getPowerShortageStart());
		myServerAgent.addBehaviour(InitiateJobTransferInCloudNetwork.create(myServerAgent, affectedJob, jobToTransfer,
				transferRequest));
	}

	private void schedulePowerShortageHandling(final JobPowerShortageTransfer jobTransfer, final ClientJob job) {
		myServerAgent.manage().divideJobForPowerShortage(job, jobTransfer.getPowerShortageStart());
		myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(singletonList(job),
				jobTransfer.getPowerShortageStart(), myServerAgent, null));
	}
}
