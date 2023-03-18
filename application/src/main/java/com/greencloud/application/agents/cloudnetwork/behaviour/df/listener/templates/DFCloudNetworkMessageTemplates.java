package com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.templates;

import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.DISABLE_SERVER_PROTOCOL;
import static com.greencloud.commons.managingsystem.executor.ExecutorMessageProtocols.ANNOUNCE_NETWORK_CHANGE_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;

import jade.lang.acl.MessageTemplate;

/**
 * Class stores all message templates used in Cloud Network listener behaviours for DF registration
 */
public class DFCloudNetworkMessageTemplates {

	public static final MessageTemplate ANNOUNCE_NETWORK_CHANGE_TEMPLATE = and(MatchPerformative(INFORM),
			MatchProtocol(ANNOUNCE_NETWORK_CHANGE_PROTOCOL));

	public static final MessageTemplate DISABLE_SERVER_TEMPLATE = and(
			or(MatchPerformative(INFORM), MatchPerformative(REQUEST)),
			MatchProtocol(DISABLE_SERVER_PROTOCOL)
	);
}
