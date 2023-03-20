package com.greencloud.application.agents.server.behaviour.jobexecution.listener.templates;

import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.CANCEL_JOB_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.FAILED_JOB_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.FAILED_TRANSFER_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.JOB_START_STATUS_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.MANUAL_JOB_FINISH_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.CANCEL;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;

import jade.lang.acl.MessageTemplate;

/**
 * Class stores all message templates used in listener behaviours for incoming job handling process
 */
public class JobHandlingMessageTemplates {

	public static final MessageTemplate NEW_JOB_CFP_TEMPLATE = and(
			MatchPerformative(CFP), MatchProtocol(CNA_JOB_CFP_PROTOCOL));
	public static final MessageTemplate POWER_SUPPLY_UPDATE_TEMPLATE = or(
			and(MatchPerformative(INFORM),
					or(MatchProtocol(SERVER_JOB_CFP_PROTOCOL), MatchProtocol(POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL))),
			and(MatchPerformative(FAILURE),
					or(MatchProtocol(FAILED_JOB_PROTOCOL), MatchProtocol(FAILED_TRANSFER_PROTOCOL))));

	public static final MessageTemplate MANUAL_FINISH_TEMPLATE = and(
			MatchPerformative(INFORM), MatchProtocol(MANUAL_JOB_FINISH_PROTOCOL));
	public static final MessageTemplate JOB_STATUS_REQUEST_TEMPLATE = and(
			MatchPerformative(REQUEST), MatchProtocol(JOB_START_STATUS_PROTOCOL));

	public static final MessageTemplate CANCEL_JOB_ANNOUNCEMENT_SERVER_TEMPLATE = and(
			MatchPerformative(CANCEL), MatchProtocol(CANCEL_JOB_PROTOCOL));
}
