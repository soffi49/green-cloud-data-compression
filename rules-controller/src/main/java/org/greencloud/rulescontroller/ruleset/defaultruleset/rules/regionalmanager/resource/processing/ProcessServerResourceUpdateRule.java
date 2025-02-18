package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.resource.processing;

import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.utils.messaging.factory.ReplyMessageFactory.prepareInformReply;
import static org.greencloud.commons.utils.messaging.factory.ReplyMessageFactory.prepareRefuseReply;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.agent.ServerResources;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ProcessServerResourceUpdateRule extends AgentBasicRule<RegionalManagerAgentProps, RegionalManagerNode> {

	private static final Logger logger = getLogger(ProcessServerResourceUpdateRule.class);

	public ProcessServerResourceUpdateRule(
			final RulesController<RegionalManagerAgentProps, RegionalManagerNode> controller) {
		super(controller);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("SERVER_RESOURCE_UPDATE_HANDLER_RULE",
				"handles server resource update",
				"rule run when resource update message was received");
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final ServerResources serverResources = facts.get(MESSAGE_CONTENT);
		final ACLMessage request = facts.get(MESSAGE);
		final AID server = request.getSender();

		if (!agentProps.getOwnedServerResources().containsKey(server)) {
			logger.info("Server {} not found in RMA. Responding with refuse to resource update.",
					server.getLocalName());
			agent.send(prepareRefuseReply(request));
			return;
		}
		if (Boolean.TRUE.equals(agentProps.getOwnedServers().get(server))) {
			logger.info("Server {} is still active in RMA. "
					+ "Maintenance cannot be performed when server is active.  "
					+ "Responding with refuse to resource update.", server.getLocalName());
			agent.send(prepareRefuseReply(request));
			return;
		}

		logger.info("RMA received information about update in resources of {}.", server.getLocalName());
		agentProps.getOwnedServerResources().replace(server, serverResources);
		agent.send(prepareInformReply(request));
	}
}
