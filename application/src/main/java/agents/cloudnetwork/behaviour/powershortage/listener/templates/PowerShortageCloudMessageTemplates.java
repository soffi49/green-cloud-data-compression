package agents.cloudnetwork.behaviour.powershortage.listener.templates;

import static messages.domain.constants.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;

import jade.lang.acl.MessageTemplate;

/**
 * Class stores all message templates used in listener behaviours for power shortage in cloud network
 */
public class PowerShortageCloudMessageTemplates {

	public static final MessageTemplate SERVER_JOB_TRANSFER_REQUEST_TEMPLATE = and(MatchPerformative(REQUEST),
			MatchProtocol(POWER_SHORTAGE_ALERT_PROTOCOL));
}
