package com.greencloud.application.agents.server.behaviour.jobexecution.listener.templates;

import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;

import com.greencloud.application.messages.domain.constants.MessageProtocolConstants;

import jade.lang.acl.MessageTemplate;

/**
 * Class stores all message templates used in listener behaviours for incoming job handling process
 */
public class JobHandlingMessageTemplates {

	public static final MessageTemplate NEW_JOB_CFP_TEMPLATE = and(MatchPerformative(CFP),
			MatchProtocol(MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL));
	public static final MessageTemplate POWER_SUPPLY_UPDATE_TEMPLATE = and(MatchPerformative(INFORM),
			or(or(MatchProtocol(MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL), MatchProtocol(
							MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL))
					, MatchProtocol(MessageProtocolConstants.MANUAL_JOB_FINISH_PROTOCOL)));
	public static final MessageTemplate JOB_STATUS_REQUEST_TEMPLATE = and(MatchPerformative(REQUEST),
			MatchProtocol(MessageProtocolConstants.JOB_START_STATUS_PROTOCOL));
}
