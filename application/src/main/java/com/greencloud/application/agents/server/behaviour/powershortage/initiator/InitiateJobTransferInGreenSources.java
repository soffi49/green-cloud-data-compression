package com.greencloud.application.agents.server.behaviour.powershortage.initiator;

import static com.greencloud.application.agents.server.behaviour.powershortage.initiator.logs.PowerShortageServerInitiatorLog.GS_TRANSFER_CHOSEN_GS_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.initiator.logs.PowerShortageServerInitiatorLog.GS_TRANSFER_FAIL_BACK_UP_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.initiator.logs.PowerShortageServerInitiatorLog.GS_TRANSFER_FAIL_NO_BACK_UP_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.initiator.logs.PowerShortageServerInitiatorLog.GS_TRANSFER_NONE_AVAILABLE_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.initiator.logs.PowerShortageServerInitiatorLog.GS_TRANSFER_NO_RESPONSE_RETRIEVED_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.BACK_UP_POWER_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageConversationConstants.ON_HOLD_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.NO_SOURCES_AVAILABLE_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.factory.CallForProposalMessageFactory.createCallForProposal;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareAcceptJobOfferReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareStringReply;
import static com.greencloud.application.utils.JobUtils.getJobByIdAndStartDate;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_BACK_UP;
import static com.greencloud.commons.domain.job.enums.JobExecutionStateEnum.EXECUTING_ON_HOLD_SOURCE;
import static jade.lang.acl.ACLMessage.REFUSE;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.powershortage.listener.ListenForSourceJobTransferConfirmation;
import com.greencloud.application.behaviours.initiator.AbstractCFPInitiator;
import com.greencloud.application.domain.agent.GreenSourceData;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStateEnum;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Behaviours sends the CFP to remaining green sources looking for job transfer and selects the one which will
 * handle the remaining job execution
 */
public class InitiateJobTransferInGreenSources extends AbstractCFPInitiator<GreenSourceData> {

	private static final Logger logger = getLogger(InitiateJobTransferInGreenSources.class);

	private final ServerAgent myServerAgent;
	private final PowerJob jobToTransfer;
	private final Instant powerShortageStart;

	public InitiateJobTransferInGreenSources(final ServerAgent agent, final ACLMessage cfp,
			final ACLMessage originalMessage, final PowerJob jobToTransfer, final Instant powerShortageStart) {
		super(agent, cfp, originalMessage, mapToJobInstanceId(jobToTransfer), agent.manage().offerComparator(),
				GreenSourceData.class);

		this.myServerAgent = agent;
		this.jobToTransfer = jobToTransfer;
		this.powerShortageStart = powerShortageStart;
	}

	/**
	 * Method creates behaviour
	 *
	 * @param agent              agent executing the behaviour
	 * @param jobToTransfer      job that is to be transferred
	 * @param greenSources       list of green sources to which CFP is sent
	 * @param greenSourceRequest original green source request
	 * @param shortageTime       time when power shortage is to start
	 * @return InitiateJobTransferInGreenSources
	 */
	public static InitiateJobTransferInGreenSources create(final ServerAgent agent, final PowerJob jobToTransfer,
			final List<AID> greenSources, final ACLMessage greenSourceRequest, final Instant shortageTime) {
		final ACLMessage cfp = createCallForProposal(jobToTransfer, greenSources, SERVER_JOB_CFP_PROTOCOL);

		return new InitiateJobTransferInGreenSources(agent, cfp, greenSourceRequest, jobToTransfer, shortageTime);
	}

	@Override
	protected void handleNoResponses() {
		logger.info(GS_TRANSFER_NO_RESPONSE_RETRIEVED_LOG);
		handleTransferFailure();
	}

	@Override
	protected void handleNoAvailableAgents() {
		logger.info(GS_TRANSFER_NONE_AVAILABLE_LOG, jobToTransfer.getJobId());
		myServerAgent.message().passTransferRequestToCloudNetwork(jobInstance, powerShortageStart, originalMessage);
	}

	@Override
	protected void handleSelectedOffer(final GreenSourceData chosenOfferData) {
		MDC.put(MDC_JOB_ID, jobInstance.getJobId());
		logger.info(GS_TRANSFER_CHOSEN_GS_LOG, jobInstance.getJobId(), bestProposal.getSender().getLocalName());

		myServerAgent.addBehaviour(new ListenForSourceJobTransferConfirmation(myServerAgent, jobInstance,
				powerShortageStart, originalMessage));
		myAgent.send(prepareAcceptJobOfferReply(bestProposal, jobInstance, POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL));
	}

	private void handleTransferFailure() {
		MDC.put(MDC_JOB_ID, jobToTransfer.getJobId());
		final ClientJob job = getJobByIdAndStartDate(jobInstance, myServerAgent.getServerJobs());

		if (nonNull(job)) {
			myServerAgent.manage().handleJobStateChange(getFieldsForJobState(job), job);
			myServerAgent.send(prepareStringReply(originalMessage, NO_SOURCES_AVAILABLE_CAUSE_MESSAGE, REFUSE));
		}
	}

	private Triple<JobExecutionStateEnum, String, String> getFieldsForJobState(final ClientJob job) {
		final int availableBackUpPower = myServerAgent.manage().getAvailableCapacity((ClientJob) jobToTransfer,
				jobInstance, EXECUTING_ON_BACK_UP.getStatuses());

		return availableBackUpPower < job.getPower() ?
				new ImmutableTriple<>(EXECUTING_ON_HOLD_SOURCE, GS_TRANSFER_FAIL_NO_BACK_UP_LOG, ON_HOLD_JOB_ID) :
				new ImmutableTriple<>(EXECUTING_ON_BACK_UP, GS_TRANSFER_FAIL_BACK_UP_LOG, BACK_UP_POWER_JOB_ID);
	}
}
