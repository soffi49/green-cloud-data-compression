package com.greencloud.application.agents.greenenergy.behaviour.adaptation;

import static com.greencloud.application.agents.greenenergy.behaviour.adaptation.logs.AdaptationGreenSourceLog.CONNECTION_FAILED_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.adaptation.logs.AdaptationGreenSourceLog.CONNECTION_SUCCEEDED_LOG;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.CONNECT_GREEN_SOURCE_PROTOCOL;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareFailureReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareInformReply;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.commons.message.MessageBuilder;
import com.gui.agents.GreenEnergyAgentNode;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Behaviour initiates connection with given server
 */
public class InitiateNewServerConnection extends AchieveREInitiator {

	private static final Logger logger = getLogger(InitiateNewServerConnection.class);

	private final GreenEnergyAgent myGreenAgent;
	private final ACLMessage adaptationMessage;

	/**
	 * Behaviours constructor
	 *
	 * @param agent             green source executing the behaviour
	 * @param connectionMessage message requesting connection in the given server
	 * @param adaptationMessage original adaptation request
	 */
	public InitiateNewServerConnection(final GreenEnergyAgent agent, final ACLMessage connectionMessage,
			final ACLMessage adaptationMessage) {
		super(agent, connectionMessage);
		this.myGreenAgent = agent;
		this.adaptationMessage = adaptationMessage;
	}

	/**
	 * Method creates the behaviour
	 *
	 * @param agent             agent executing the behaviour
	 * @param adaptationMessage original adaptation request
	 * @param serverAID         identifier of the Server Agent with which the Green Source is to be connected
	 * @return InitiateNewServerConnection
	 */
	public static InitiateNewServerConnection create(final GreenEnergyAgent agent, final ACLMessage adaptationMessage,
			final String serverAID) {
		final ACLMessage connectionRequest = MessageBuilder.builder()
				.withPerformative(REQUEST)
				.withMessageProtocol(CONNECT_GREEN_SOURCE_PROTOCOL)
				.withStringContent(CONNECT_GREEN_SOURCE_PROTOCOL)
				.withReceivers(new AID(serverAID, AID.ISGUID))
				.build();
		return new InitiateNewServerConnection(agent, connectionRequest, adaptationMessage);
	}

	/**
	 * Method handles the REFUSE response retrieved from Server informing that the Green Source is already connected
	 *
	 * @param refuse retrieved response
	 */
	@Override
	protected void handleRefuse(ACLMessage refuse) {
		logger.info(CONNECTION_FAILED_LOG, refuse.getSender().getName());
		myGreenAgent.send(prepareFailureReply(adaptationMessage));
	}

	/**
	 * Method handles the INFORM response retrieved from Server informing that the connection was successful
	 *
	 * @param inform retrieved response
	 */
	@Override
	protected void handleInform(ACLMessage inform) {
		logger.info(CONNECTION_SUCCEEDED_LOG, inform.getSender().getName());
		myGreenAgent.send(prepareInformReply(adaptationMessage));

		if (nonNull(myGreenAgent.getAgentNode())) {
			((GreenEnergyAgentNode) myGreenAgent.getAgentNode()).updateServerConnection(
					inform.getSender().getName().split("@")[0], true);
		}
	}

}
