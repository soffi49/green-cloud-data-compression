package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.listening;

import static java.util.Optional.ofNullable;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_COMPRESSION;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_ID;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE;
import static org.greencloud.commons.utils.job.JobUtils.getJobById;
import static org.greencloud.rulescontroller.rule.combined.domain.AgentCombinedRuleType.EXECUTE_FIRST;

import java.util.List;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.transfer.JobPowerShortageTransfer;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.combined.AgentCombinedRule;
import org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.listening.processing.ProcessTransferRequestJobNotFoundRule;
import org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.listening.processing.ProcessTransferRequestJobPresentCombinedRule;

public class ProcessTransferRequestCombinedRule extends AgentCombinedRule<RegionalManagerAgentProps, RegionalManagerNode> {

	public ProcessTransferRequestCombinedRule(
			final RulesController<RegionalManagerAgentProps, RegionalManagerNode> controller) {
		super(controller, EXECUTE_FIRST);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE,
				"transfer job handler",
				"handles request to transfer job from one Server to another");
	}

	@Override
	protected List<AgentRule> constructRules() {
		return List.of(
				new ProcessTransferRequestJobNotFoundRule(controller),
				new ProcessTransferRequestJobPresentCombinedRule(controller).getRules().get(0)
		);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final JobPowerShortageTransfer transferData = facts.get(MESSAGE_CONTENT);
		final String jobId = transferData.getSecondJobInstanceId().getJobId();
		final ClientJob job = getJobById(jobId, agentProps.getNetworkJobs());

		facts.put(JOB, ofNullable(job));
		facts.put(JOB_COMPRESSION, ofNullable(transferData.getDataInputInformation()));
		facts.put(JOB_ID, jobId);
	}
}
