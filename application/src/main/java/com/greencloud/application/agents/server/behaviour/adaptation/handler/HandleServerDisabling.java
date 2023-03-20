package com.greencloud.application.agents.server.behaviour.adaptation.handler;

import static com.greencloud.application.agents.server.behaviour.adaptation.handler.logs.ServerAdaptationHandlerLog.DISABLING_COMPLETED_LOG;
import static com.greencloud.application.messages.constants.MessageProtocolConstants.DISABLE_SERVER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.commons.message.MessageBuilder;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour completes action of the server disabling
 */
public class HandleServerDisabling extends OneShotBehaviour {

	private static final Logger logger = getLogger(HandleServerDisabling.class);

	private ServerAgent myServerAgent;

	/**
	 * Method casts abstract agent to agent of type Server Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method sends the information to parent Cloud Network that confirms that the Server was fully disabled
	 */
	@Override
	public void action() {
		logger.info(DISABLING_COMPLETED_LOG);
		final ACLMessage disableServerMessage = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withMessageProtocol(DISABLE_SERVER_PROTOCOL)
				.withStringContent(valueOf(myServerAgent.getInitialMaximumCapacity()))
				.withReceivers(myServerAgent.getOwnerCloudNetworkAgent())
				.build();

		myServerAgent.send(disableServerMessage);
	}
}
