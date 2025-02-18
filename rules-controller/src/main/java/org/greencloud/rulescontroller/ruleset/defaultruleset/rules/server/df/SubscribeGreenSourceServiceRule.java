package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.df;

import static org.greencloud.commons.constants.DFServiceConstants.GS_SERVICE_TYPE;
import static org.greencloud.commons.enums.rules.RuleType.SUBSCRIBE_OWNED_AGENTS_SERVICE_RULE;
import static org.greencloud.commons.utils.yellowpages.YellowPagesRegister.prepareSubscription;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentSubscriptionRule;
import org.slf4j.Logger;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SubscribeGreenSourceServiceRule extends AgentSubscriptionRule<ServerAgentProps, ServerNode> {

	private static final Logger logger = getLogger(SubscribeGreenSourceServiceRule.class);

	public SubscribeGreenSourceServiceRule(final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(SUBSCRIBE_OWNED_AGENTS_SERVICE_RULE,
				"subscribe Green Source Agent service",
				"handle changes in Green Source Agent service");
	}

	@Override
	protected ACLMessage createSubscriptionMessage(final RuleSetFacts facts) {
		return prepareSubscription(agent, agent.getDefaultDF(), GS_SERVICE_TYPE, agent.getName());
	}

	@Override
	protected void handleRemovedAgents(final Map<AID, Boolean> removedAgents) {
		logger.info("Received message that {} Green Source deregistered its service", removedAgents.size());
		removedAgents.keySet().forEach(agent -> {
			agentProps.getOwnedGreenSources().remove(agent);
			agentProps.getWeightsForGreenSourcesMap().remove(agent);
		});
	}

	@Override
	protected void handleAddedAgents(final Map<AID, Boolean> addedAgents) {
		logger.info("Received message that {} new Green Source registered its service", addedAgents.size());
		agentProps.connectNewGreenSourcesToServer(addedAgents.keySet().stream().toList());
	}
}
