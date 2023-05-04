package com.greencloud.application.behaviours.listener;

import static com.greencloud.application.behaviours.logs.AbstractBehavioursLogs.MESSAGE_SENDING_FAILED;
import static com.greencloud.commons.constants.LoggingConstant.MDC_JOB_ID;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.MatchInReplyTo;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.greencloud.application.agents.AbstractAgent;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;
import jade.util.leap.Iterator;

/**
 * Behaviour listens for AMS communication failures
 */
public class ListenForAMSError extends MsgReceiver {

	public static final long FAILURE_EXPIRATION_TIME = 60000;

	private static final Logger logger = getLogger(ListenForAMSError.class);

	private final AbstractAgent myAbstractAgent;
	private final ACLMessage message;
	private final String jobId;

	private ListenForAMSError(final AbstractAgent agent, final MessageTemplate template,
			final ACLMessage message, final String jobId) {
		super(agent, template, FAILURE_EXPIRATION_TIME + currentTimeMillis(), null, null);
		this.myAbstractAgent = agent;
		this.message = message;
		this.jobId = jobId;
	}

	/**
	 * Method creates the behaviour.
	 *
	 * @param agent   agent executing the behaviour
	 * @param message message of interest
	 * @param jobId   identifier of the job of interest
	 */
	public static ListenForAMSError create(final AbstractAgent agent, final ACLMessage message,
			final String jobId) {
		final AID ams = getAMS(message.getAllReceiver());
		final MessageTemplate template = nonNull(message.getReplyWith())
				? and(and(MatchSender(ams), MatchConversationId(message.getConversationId())),
				and(MatchPerformative(FAILURE), MatchInReplyTo(message.getReplyWith())))
				: and(and(MatchSender(ams), MatchConversationId(message.getConversationId())),
				MatchPerformative(FAILURE));
		return new ListenForAMSError(agent, template, message, jobId);
	}

	private static AID getAMS(final Iterator receivers) {
		final List<AID> receiversList = new ArrayList<>();
		receivers.forEachRemaining(receiver -> receiversList.add((AID) receiver));

		final String platformName = receiversList.get(0).getName().split("@")[1];
		final String address = receiversList.get(0).getAddressesArray()[0];

		final AID ams = new AID("ams@" + platformName, AID.ISGUID);
		ams.addAddresses(address);
		return ams;
	}

	/**
	 * Method listens for the messages coming from the AMS agent informing that sending
	 * given job proposal failed (i.e. Scheduler was not found).
	 * Method attempts to resend the given proposal
	 */
	@Override
	protected void handleMessage(ACLMessage msg) {
		if (nonNull(msg)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.info(MESSAGE_SENDING_FAILED);
			myAbstractAgent.send(message);
		}
	}
}
