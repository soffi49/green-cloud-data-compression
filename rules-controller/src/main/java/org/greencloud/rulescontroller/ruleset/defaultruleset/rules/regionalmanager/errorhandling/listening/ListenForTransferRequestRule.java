package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.listening;

import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageTemplatesConstants.LISTEN_FOR_SERVER_TRANSFER_REQUEST_TEMPLATE;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.job.transfer.JobPowerShortageTransfer;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentMessageListenerRule;
import org.greencloud.rulescontroller.ruleset.RuleSet;

public class ListenForTransferRequestRule extends AgentMessageListenerRule<RegionalManagerAgentProps, RegionalManagerNode> {

	public ListenForTransferRequestRule(final RulesController<RegionalManagerAgentProps, RegionalManagerNode> controller,
			final RuleSet ruleSet) {
		super(controller, ruleSet, JobPowerShortageTransfer.class, LISTEN_FOR_SERVER_TRANSFER_REQUEST_TEMPLATE, 50,
				LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(LISTEN_FOR_JOB_TRANSFER_RULE,
				"transfer job listener",
				"listens for request to transfer job from one Server to another");
	}
}
