package agents.server.behaviour.powershortage.announcer;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.MessagingUtils.retrieveValidMessages;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageTransferRequest;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.listener.ListenForSourceJobTransferConfirmation;
import agents.server.behaviour.powershortage.transfer.RequestJobTransferInCloudNetwork;
import domain.GreenSourceData;
import domain.job.PowerShortageJob;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import messages.domain.ReplyMessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

/**
 * Behaviours responsible for sending the transfer call for proposal to remaining green sources and choosing the one which
 * will handle the job when the power shortage happens
 */
public class AnnounceSourceJobTransfer extends ContractNetInitiator {

	private static final Logger logger = LoggerFactory.getLogger(AnnounceSourceJobTransfer.class);

	private final ServerAgent myServerAgent;
	private final PowerShortageJob jobTransfer;
	private final ACLMessage greenSourceRequest;

	/**
	 * Behaviour constructor
	 *
	 * @param agent              agent which executes the behaviour
	 * @param powerRequest       call for proposal containing the details regarding power needed to execute the job
	 * @param greenSourceRequest green source power transfer request
	 * @param jobTransfer        data regarding the job transfer
	 */
	public AnnounceSourceJobTransfer(final Agent agent,
			final ACLMessage powerRequest,
			final ACLMessage greenSourceRequest,
			final PowerShortageJob jobTransfer) {
		super(agent, powerRequest);
		this.myServerAgent = (ServerAgent) myAgent;
		this.jobTransfer = jobTransfer;
		this.greenSourceRequest = greenSourceRequest;
	}

	/**
	 * Method which waits for all Green Source Agent responses. It is responsible for analyzing the received proposals,
	 * choosing the Green Source Agent for power job transfer execution and rejecting the remaining Green Source Agents.
	 * If no green source is available, it passes the information about the need of the job transfer to the parent Cloud Network
	 *
	 * @param responses   retrieved responses from Green Source Agents
	 * @param acceptances vector containing accept proposal message sent back to the chosen green source (not used)
	 */
	@Override
	protected void handleAllResponses(Vector responses, Vector acceptances) {
		final List<ACLMessage> proposals = retrieveProposals(responses);

		if (responses.isEmpty()) {
			logger.info("[{}] No responses were retrieved", myAgent.getName());
			myServerAgent.send(
					prepareReply(greenSourceRequest.createReply(), jobTransfer.getJobInstanceId(), ACLMessage.FAILURE));
		} else if (proposals.isEmpty()) {
			logger.info(
					"[{}] No green sources are available for the power transfer of job {}. Passing the information to the cloud network",
					myAgent.getName(), jobTransfer.getJobInstanceId().getJobId());
			final ACLMessage transferMessage = preparePowerShortageTransferRequest(jobTransfer,
					myServerAgent.getOwnerCloudNetworkAgent());
			displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
			myServerAgent.addBehaviour(
					new RequestJobTransferInCloudNetwork(myServerAgent, transferMessage, greenSourceRequest,
							jobTransfer, false));
		} else {
			final List<ACLMessage> validProposals = retrieveValidMessages(proposals, GreenSourceData.class);
			if (!validProposals.isEmpty()) {
				final ACLMessage chosenGreenSourceOffer = myServerAgent.chooseGreenSourceToExecuteJob(validProposals);
				final String jobId = jobTransfer.getJobInstanceId().getJobId();
				logger.info("[{}] Chosen Green Source for the job {} transfer: {}", myAgent.getName(), jobId,
						chosenGreenSourceOffer.getSender().getLocalName());

				displayMessageArrow(myServerAgent, myServerAgent.getGreenSourceForJobMap().get(jobId));
				displayMessageArrow(myServerAgent, chosenGreenSourceOffer.getAllReceiver());

				myServerAgent.addBehaviour(
						new ListenForSourceJobTransferConfirmation(myServerAgent, jobTransfer.getJobInstanceId(),
								greenSourceRequest));
				myAgent.send(ReplyMessageFactory.prepareAcceptReplyWithProtocol(chosenGreenSourceOffer.createReply(),
						jobTransfer.getJobInstanceId(), POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL));
				rejectJobOffers(myServerAgent, jobTransfer.getJobInstanceId(), chosenGreenSourceOffer, proposals);
			} else {
				handleInvalidProposals(proposals);
			}
		}
	}

	private void handleInvalidProposals(final List<ACLMessage> proposals) {
		logger.info("I didn't understand any proposal from Green Energy Agents");
		myServerAgent.send(
				prepareReply(greenSourceRequest.createReply(), jobTransfer.getJobInstanceId(), ACLMessage.FAILURE));
		rejectJobOffers(myServerAgent, jobTransfer.getJobInstanceId(), null, proposals);
	}
}
