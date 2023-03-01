package com.greencloud.application.agents.cloudnetwork.behaviour.df;

import static com.greencloud.application.agents.cloudnetwork.behaviour.df.logs.CloudNetworkDFLog.UPDATE_MAX_CAPACITY_LOG;
import static com.greencloud.application.messages.MessagingUtils.retrieveForPerformative;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.ASK_FOR_POWER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;

import java.util.Collection;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.domain.CloudNetworkPowerUpdateType;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.commons.message.MessageBuilder;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

/**
 * Behaviour sends a request to the given servers asking for information regarding maximum capacity provided by their services
 */
public class AskServerForPowerInformation extends AchieveREInitiator {

	private static final Logger logger = LoggerFactory.getLogger(AskServerForPowerInformation.class);

	private final CloudNetworkAgent myCloudNetwork;
	private final CloudNetworkPowerUpdateType powerUpdateType;

	/**
	 * Behaviour constructor
	 *
	 * @param agent           agent executing the behaviour
	 * @param msg             request that is to be sent to the server agent
	 * @param powerUpdateType defines a way in which Cloud Network should update its power
	 */
	private AskServerForPowerInformation(final Agent agent, final ACLMessage msg,
			final CloudNetworkPowerUpdateType powerUpdateType) {
		super(agent, msg);
		this.myCloudNetwork = (CloudNetworkAgent) agent;
		this.powerUpdateType = powerUpdateType;
	}

	/**
	 * Method created AskServerForPowerInformation behaviour
	 *
	 * @param cloudNetworkAgent agent executing the behaviour
	 * @param serversToAsk      servers that are to be asked for power
	 * @param powerUpdateType   defines a way in which Cloud Network should update its power
	 * @return AskServerForPowerInformation
	 */
	public static AskServerForPowerInformation create(final CloudNetworkAgent cloudNetworkAgent,
			final Set<AID> serversToAsk, final CloudNetworkPowerUpdateType powerUpdateType) {
		final ACLMessage message = MessageBuilder.builder()
				.withMessageProtocol(ASK_FOR_POWER_PROTOCOL)
				.withStringContent(ASK_FOR_POWER_PROTOCOL)
				.withPerformative(REQUEST)
				.withReceivers(serversToAsk)
				.build();
		return new AskServerForPowerInformation(cloudNetworkAgent, message, powerUpdateType);
	}

	/**
	 * Method handles the INFORM messages containing the information regarding servers maximum capacities
	 *
	 * @param resultNotifications vector of retrieved results
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void handleAllResultNotifications(Vector resultNotifications) {
		final Collection<ACLMessage> informs = retrieveForPerformative((Vector<ACLMessage>) resultNotifications,
				INFORM);

		logger.info(UPDATE_MAX_CAPACITY_LOG);
		myCloudNetwork.manage().updateMaximumCapacity(getNewCapacity(informs));
	}

	private double getNewCapacity(final Collection<ACLMessage> informs) {
		final double capacity = informs.stream().mapToDouble(this::getCapacity).sum();

		return switch (powerUpdateType) {
			case UPDATE_ALL -> capacity;
			case DECREMENT_CAPACITY -> myCloudNetwork.getMaximumCapacity() - capacity;
			case INCREMENT_CAPACITY -> myCloudNetwork.getMaximumCapacity() + capacity;
		};
	}

	private double getCapacity(ACLMessage inform) {
		try {
			return Double.parseDouble(inform.getContent());
		} catch (NumberFormatException e) {
			throw new IncorrectMessageContentException();
		}
	}
}
