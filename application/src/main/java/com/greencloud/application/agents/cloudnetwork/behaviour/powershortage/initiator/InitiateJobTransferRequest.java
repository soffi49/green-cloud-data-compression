package com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.initiator;

import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.initiator.logs.PowerShortageCloudInitiatorLog.SERVER_TRANSFER_CHOSEN_SERVER_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.initiator.logs.PowerShortageCloudInitiatorLog.SERVER_TRANSFER_NO_RESPONSE_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.initiator.logs.PowerShortageCloudInitiatorLog.SERVER_TRANSFER_NO_SERVERS_AVAILABLE_LOG;
import static com.greencloud.application.mapper.JobMapper.mapToPowerShortageJob;
import static com.greencloud.application.messages.domain.constants.MessageContentConstants.NO_SERVER_AVAILABLE_CAUSE_MESSAGE;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.CallForProposalMessageFactory.createCallForProposal;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareAcceptJobOfferReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareReply;
import static jade.lang.acl.ACLMessage.FAILURE;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerTransferConfirmation;
import com.greencloud.application.behaviours.initiator.AbstractCFPInitiator;
import com.greencloud.application.domain.agent.ServerData;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.commons.domain.job.ClientJob;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Behaviours sends the CFP to remaining servers looking for job transfer and selects the one which will
 * handle the remaining job execution
 */
public class InitiateJobTransferRequest extends AbstractCFPInitiator<ServerData> {

	private static final Logger logger = getLogger(InitiateJobTransferRequest.class);

	private final JobPowerShortageTransfer jobTransfer;
	private final CloudNetworkAgent myCloudNetworkAgent;

	private InitiateJobTransferRequest(final CloudNetworkAgent agent, final ACLMessage cfp,
			final ACLMessage serverRequest, final JobPowerShortageTransfer jobTransfer) {
		super(agent, cfp, serverRequest, jobTransfer.getJobInstanceId(), agent.manage().offerComparator(),
				ServerData.class);

		this.jobTransfer = jobTransfer;
		this.myCloudNetworkAgent = agent;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param agent             agent which is executing the behaviour
	 * @param serverRequest     transfer request message received from the server
	 * @param jobToTransfer     job for which the transfer is being performed
	 * @param shortageStartTime time when the power shortage will start
	 * @param servers           servers that are to be asked for job transfer
	 * @return InitiateJobTransferRequest
	 */
	public static InitiateJobTransferRequest create(final CloudNetworkAgent agent, final ACLMessage serverRequest,
			final ClientJob jobToTransfer, final Instant shortageStartTime, final List<AID> servers) {
		final JobPowerShortageTransfer jobTransfer = mapToPowerShortageJob(jobToTransfer, shortageStartTime);
		final ACLMessage cfp = createCallForProposal(jobToTransfer, servers, CNA_JOB_CFP_PROTOCOL);
		return new InitiateJobTransferRequest(agent, cfp, serverRequest, jobTransfer);
	}

	/**
	 * Method logs message regarding no responses and calls generic method responsible for postprocessing the job
	 * transfer failure.
	 */
	@Override
	protected void handleNoResponses() {
		logger.info(SERVER_TRANSFER_NO_RESPONSE_LOG);
		respondWithFailureMessage();
	}

	/**
	 * Method logs message regarding no available servers and calls generic method responsible for postprocessing the
	 * job transfer failure.
	 */
	@Override
	protected void handleNoAvailableAgents() {
		logger.info(SERVER_TRANSFER_NO_SERVERS_AVAILABLE_LOG);
		respondWithFailureMessage();
	}

	/**
	 * Method accepts the job transfer proposal for the previously selected best offer and initiates listener that
	 * waits for the message that will enclose the negotiation by confirming the transfer on the Server side.
	 */
	@Override
	protected void handleSelectedOffer(final ServerData serverData) {
		final AID chosenServer = bestProposal.getSender();
		logger.info(SERVER_TRANSFER_CHOSEN_SERVER_LOG, serverData.getJobId(), chosenServer.getName());

		final ACLMessage replyToChosenOffer = prepareAcceptJobOfferReply(bestProposal, jobTransfer.getJobInstanceId(),
				POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL);
		myAgent.addBehaviour(ListenForServerTransferConfirmation.create(myCloudNetworkAgent, originalMessage,
				jobTransfer, chosenServer));
		myAgent.send(replyToChosenOffer);
	}

	private void respondWithFailureMessage() {
		final ACLMessage response = prepareReply(originalMessage, NO_SERVER_AVAILABLE_CAUSE_MESSAGE, FAILURE);
		myCloudNetworkAgent.send(response);
	}
}
