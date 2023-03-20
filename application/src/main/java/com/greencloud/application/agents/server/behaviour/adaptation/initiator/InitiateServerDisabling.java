package com.greencloud.application.agents.server.behaviour.adaptation.initiator;

import static com.greencloud.application.agents.server.behaviour.adaptation.initiator.logs.AdaptationServerLog.DISABLING_FAILED_LOG;
import static com.greencloud.application.agents.server.behaviour.adaptation.initiator.logs.AdaptationServerLog.DISABLING_LEFT_JOBS_LOG;
import static com.greencloud.application.agents.server.behaviour.adaptation.initiator.logs.AdaptationServerLog.DISABLING_SUCCEEDED_LOG;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.DISABLE_SERVER_PROTOCOL;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareFailureReply;
import static com.greencloud.application.messages.factory.ReplyMessageFactory.prepareInformReply;
import static jade.lang.acl.ACLMessage.REQUEST;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.adaptation.handler.HandleServerDisabling;
import com.greencloud.commons.message.MessageBuilder;

import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Behaviour requests in the parent CNA that the server should be disabled
 */
public class InitiateServerDisabling extends AchieveREInitiator {

	private static final Logger logger = getLogger(InitiateServerDisabling.class);

	private final ServerAgent myServerAgent;
	private final ACLMessage adaptationMessage;

	private InitiateServerDisabling(final ServerAgent agent, final ACLMessage disablingMessage,
			final ACLMessage adaptationMessage) {
		super(agent, disablingMessage);

		this.myServerAgent = agent;
		this.adaptationMessage = adaptationMessage;
	}

	/**
	 * Method creating a behaviour
	 *
	 * @param myServerAgent     server executing the behaviour
	 * @param adaptationMessage original adaptation request
	 * @return new InitiateServerDisabling behaviour
	 */
	public static InitiateServerDisabling create(final ServerAgent myServerAgent, final ACLMessage adaptationMessage) {
		final ACLMessage disablingMessage = MessageBuilder.builder()
				.withPerformative(REQUEST)
				.withMessageProtocol(DISABLE_SERVER_PROTOCOL)
				.withStringContent(DISABLE_SERVER_PROTOCOL)
				.withReceivers(myServerAgent.getOwnerCloudNetworkAgent())
				.build();
		return new InitiateServerDisabling(myServerAgent, disablingMessage, adaptationMessage);
	}

	/**
	 * Method handles the INFORM response retrieved from CNA informing that the Server was successfully
	 * disabled
	 *
	 * @param inform retrieved response
	 */
	@Override
	protected void handleInform(final ACLMessage inform) {
		logger.info(DISABLING_SUCCEEDED_LOG, inform.getSender().getName());
		myServerAgent.send(prepareInformReply(adaptationMessage));

		if (myServerAgent.getServerJobs().size() > 0) {
			logger.info(DISABLING_LEFT_JOBS_LOG, myServerAgent.getServerJobs().size());
			return;
		}

		myServerAgent.addBehaviour(new HandleServerDisabling());
	}

	/**
	 * Method handles the REFUSE response retrieved from CNA informing that the Server does not exist in a Cloud
	 * Network
	 *
	 * @param refuse retrieved response
	 */
	@Override
	protected void handleRefuse(final ACLMessage refuse) {
		logger.info(DISABLING_FAILED_LOG, refuse.getSender().getName());
		myServerAgent.enable();
		myServerAgent.send(prepareFailureReply(adaptationMessage));
	}

}
