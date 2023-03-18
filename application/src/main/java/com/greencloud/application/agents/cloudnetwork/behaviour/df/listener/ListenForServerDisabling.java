package com.greencloud.application.agents.cloudnetwork.behaviour.df.listener;

import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.logs.CloudNetworkDFListenerLog.DISABLING_SERVER_IN_CNA_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.logs.CloudNetworkDFListenerLog.SERVER_FOR_DISABLING_NOT_FOUND_LOG;
import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.templates.DFCloudNetworkMessageTemplates.DISABLE_SERVER_TEMPLATE;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareInformReply;
import static com.greencloud.application.messages.domain.factory.ReplyMessageFactory.prepareRefuseReply;
import static com.greencloud.application.utils.StateManagementUtils.updateAgentMaximumCapacity;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static java.lang.Double.parseDouble;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Objects;

import org.slf4j.Logger;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the messages informing about disabling given Server
 */
public class ListenForServerDisabling extends CyclicBehaviour {

	private static final Logger logger = getLogger(ListenForServerDisabling.class);

	private final CloudNetworkAgent myCloudNetworkAgent;

	/**
	 * Default constructor
	 *
	 * @param myCloudNetworkAgent agent executing behaviour
	 */
	public ListenForServerDisabling(CloudNetworkAgent myCloudNetworkAgent) {
		super(myCloudNetworkAgent);
		this.myCloudNetworkAgent = myCloudNetworkAgent;
	}

	/**
	 * Method listens for the message coming from the Servers informing that a given server is going to be disabled
	 */
	@Override
	public void action() {
		final ACLMessage msg = myCloudNetworkAgent.receive(DISABLE_SERVER_TEMPLATE);

		if (Objects.nonNull(msg)) {
			switch (msg.getPerformative()) {
				case REQUEST -> handleServerDisablingRequest(msg);
				case INFORM -> handleServerDisablingCompletion(msg);
				default -> block();
			}
		} else {
			block();
		}
	}

	private void handleServerDisablingRequest(final ACLMessage request) {
		final AID serverToBeDisabled = request.getSender();

		if (myCloudNetworkAgent.getOwnedServers().containsKey(serverToBeDisabled)) {
			logger.info(DISABLING_SERVER_IN_CNA_LOG, serverToBeDisabled.getLocalName());
			myCloudNetworkAgent.getOwnedServers().replace(serverToBeDisabled, false);
			myCloudNetworkAgent.send(prepareInformReply(request));
		} else {
			logger.info(SERVER_FOR_DISABLING_NOT_FOUND_LOG, serverToBeDisabled.getLocalName());
			myCloudNetworkAgent.send(prepareRefuseReply(request));
		}
	}

	private void handleServerDisablingCompletion(final ACLMessage msg) {
		final double newNetworkCapacity = myCloudNetworkAgent.getMaximumCapacity() - parseDouble(msg.getContent());
		updateAgentMaximumCapacity(newNetworkCapacity, myCloudNetworkAgent);
	}

}
