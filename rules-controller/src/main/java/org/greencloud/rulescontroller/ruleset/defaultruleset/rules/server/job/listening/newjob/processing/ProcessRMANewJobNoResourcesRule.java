package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.job.listening.newjob.processing;

import static java.lang.String.valueOf;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.RESOURCES;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_RULE_SET_ID;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLE_NO_RESOURCES_RULE;
import static org.greencloud.commons.utils.messaging.factory.ReplyMessageFactory.prepareRefuseReply;
import static org.greencloud.commons.utils.resources.ResourcesUtilization.areSufficient;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.resources.Resource;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

import jade.lang.acl.ACLMessage;

public class ProcessRMANewJobNoResourcesRule extends AgentBasicRule<ServerAgentProps, ServerNode> {

	private static final Logger logger = getLogger(ProcessRMANewJobNoResourcesRule.class);

	public ProcessRMANewJobNoResourcesRule(final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller, 3);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_RECEIVER_HANDLER_RULE, NEW_JOB_RECEIVER_HANDLE_NO_RESOURCES_RULE,
				"handles new RMA job request - no resources",
				"handling new job sent by RMA");
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		final ClientJob job = facts.get(JOB);
		final Map<String, Resource> resources = facts.get(RESOURCES);
		return !areSufficient(resources, job.getRequiredResources())
				|| !agentProps.canTakeIntoProcessing()
				|| agentProps.getOwnedGreenSources().isEmpty()
				|| agentProps.isHasError();
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final ACLMessage message = facts.get(MESSAGE);
		final ClientJob job = facts.get(JOB);

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));
		logger.info("Not enough available resources! Sending refuse message to Regional Manager Agent");
		agent.send(prepareRefuseReply(message));
	}
}
