package agents.cloudnetwork.behaviour.powershortage.announcer;

import static agents.cloudnetwork.behaviour.powershortage.announcer.logs.PowerShortageCloudAnnouncerLog.SERVER_TRANSFER_CHOSEN_SERVER_LOG;
import static agents.cloudnetwork.behaviour.powershortage.announcer.logs.PowerShortageCloudAnnouncerLog.SERVER_TRANSFER_NO_RESPONSE_LOG;
import static agents.cloudnetwork.behaviour.powershortage.announcer.logs.PowerShortageCloudAnnouncerLog.SERVER_TRANSFER_NO_SERVERS_AVAILABLE_LOG;
import static utils.GUIUtils.displayMessageArrow;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.readMessageContent;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.MessagingUtils.retrieveValidMessages;
import static messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL;
import static messages.domain.constants.PowerShortageMessageContentConstants.NO_SERVER_AVAILABLE_CAUSE_MESSAGE;
import static messages.domain.constants.PowerShortageMessageContentConstants.TRANSFER_SUCCESSFUL_MESSAGE;
import static messages.domain.factory.ReplyMessageFactory.prepareAcceptReplyWithProtocol;
import static messages.domain.factory.ReplyMessageFactory.prepareReply;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.behaviour.powershortage.handler.HandleJobTransferToServer;
import domain.ServerData;
import domain.powershortage.PowerShortageJob;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

/**
 * Behaviours sends the CFP to remaining servers looking for job transfer and selects the one which will
 * handle the remaining job execution
 */
public class AnnounceJobTransferRequest extends ContractNetInitiator {
	private static final Logger logger = LoggerFactory.getLogger(AnnounceJobTransferRequest.class);

	private final CloudNetworkAgent myCloudNetworkAgent;
	private final String guid;
	private final ACLMessage serverRequest;
	private final PowerShortageJob jobTransfer;
	private final AID jobClient;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent         agent which is executing the behaviour
	 * @param cfp           call for proposal message containing job requriements sent to the servers
	 * @param serverRequest transfer request message coming from the server
	 * @param jobTransfer   job for which the transfer is being performed
	 * @param jobClient     client which should be informed about job status updates
	 */
	public AnnounceJobTransferRequest(final Agent agent,
			final ACLMessage cfp,
			final ACLMessage serverRequest,
			final PowerShortageJob jobTransfer,
			final String jobClient) {
		super(agent, cfp);
		this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
		this.guid = agent.getName();
		this.jobTransfer = jobTransfer;
		this.serverRequest = serverRequest;
		this.jobClient = new AID(jobClient, AID.ISGUID);
	}

	/**
	 * Method processes Server Agent responses.
	 * It selects one server to which the job will be transferred.
	 * If no servers are available, it sends the information to server with power shortage that the transfer was
	 * unsuccessful.
	 *
	 * @param responses   retrieved responses from Server Agents
	 * @param acceptances vector containing accept proposal message sent back to the chosen server (not used)
	 */
	@Override
	protected void handleAllResponses(final Vector responses, final Vector acceptances) {
		final List<ACLMessage> proposals = retrieveProposals(responses);

		if (responses.isEmpty()) {
			logger.info(SERVER_TRANSFER_NO_RESPONSE_LOG, guid);
			respondWithFailureMessage();
		} else if (proposals.isEmpty()) {
			logger.info(SERVER_TRANSFER_NO_SERVERS_AVAILABLE_LOG, guid);
			respondWithFailureMessage();
		} else {
			final List<ACLMessage> validProposals = retrieveValidMessages(proposals, ServerData.class);

			if (!validProposals.isEmpty()) {
				final ACLMessage chosenServerOffer = chooseServerToExecuteJob(validProposals);
				final ServerData chosenServerData = readMessageContent(chosenServerOffer, ServerData.class);
				final AID chosenServer = chosenServerOffer.getSender();
				logger.info(SERVER_TRANSFER_CHOSEN_SERVER_LOG, guid, chosenServerData.getJobId(),
						chosenServer.getName());

				initiateTransferForServer(chosenServer, chosenServerOffer);
				rejectJobOffers(myCloudNetworkAgent, jobTransfer.getJobInstanceId(), chosenServerOffer, proposals);
			} else {
				handleInvalidResponses(proposals);
			}
		}
	}

	private void initiateTransferForServer(final AID chosenServer, final ACLMessage chosenOffer) {
		final ACLMessage replyToChosenOffer = prepareAcceptReplyWithProtocol(chosenOffer.createReply(),
				jobTransfer.getJobInstanceId(), POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL);
		final ACLMessage replyToServerRequest = prepareReply(serverRequest.createReply(), TRANSFER_SUCCESSFUL_MESSAGE,
				ACLMessage.INFORM);

		displayMessageArrow(myCloudNetworkAgent, chosenServer);
		displayMessageArrow(myCloudNetworkAgent, serverRequest.getSender());

		myAgent.send(replyToChosenOffer);
		myCloudNetworkAgent.send(replyToServerRequest);
		myCloudNetworkAgent.addBehaviour(
				HandleJobTransferToServer.createFor(myCloudNetworkAgent, jobTransfer, chosenServer));

	}

	private void handleInvalidResponses(final List<ACLMessage> proposals) {
		rejectJobOffers(myCloudNetworkAgent, jobTransfer.getJobInstanceId(), null, proposals);
		respondWithFailureMessage();
	}

	private void respondWithFailureMessage() {
		final ACLMessage response = prepareReply(serverRequest.createReply(), NO_SERVER_AVAILABLE_CAUSE_MESSAGE,
				ACLMessage.FAILURE);
		myCloudNetworkAgent.send(response);
	}

	private ACLMessage chooseServerToExecuteJob(final List<ACLMessage> serverOffers) {
		return serverOffers.stream().min(this::compareServerOffers).orElseThrow();
	}

	private int compareServerOffers(final ACLMessage serverOffer1, final ACLMessage serverOffer2) {
		final ServerData server1;
		final ServerData server2;
		try {
			server1 = getMapper().readValue(serverOffer1.getContent(), ServerData.class);
			server2 = getMapper().readValue(serverOffer2.getContent(), ServerData.class);
			return server1.getAvailablePower() - server2.getAvailablePower();
		} catch (JsonProcessingException e) {
			return Integer.MAX_VALUE;
		}
	}
}
