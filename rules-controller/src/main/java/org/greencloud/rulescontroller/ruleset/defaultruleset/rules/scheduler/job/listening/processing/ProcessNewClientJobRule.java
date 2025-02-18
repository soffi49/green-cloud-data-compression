package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.scheduler.job.listening.processing;

import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;
import static org.greencloud.commons.args.agent.AgentType.SCHEDULER;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_RULE_SET_ID;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.CREATED;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLE_JOB_DUPLICATE_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.SCHEDULED_JOB_ID;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static org.greencloud.rulescontroller.ruleset.RuleSetConstructor.constructRuleSetForCustomClientComparison;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.commons.args.agent.scheduler.agent.SchedulerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.gui.agents.scheduler.SchedulerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rest.domain.RuleSetRest;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.greencloud.rulescontroller.ruleset.RuleSet;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class ProcessNewClientJobRule extends AgentBasicRule<SchedulerAgentProps, SchedulerNode> {

	private static final Logger logger = getLogger(ProcessNewClientJobRule.class);

	public ProcessNewClientJobRule(final RulesController<SchedulerAgentProps, SchedulerNode> controller) {
		super(controller, 1);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_RECEIVER_HANDLER_RULE, NEW_JOB_RECEIVER_HANDLE_JOB_DUPLICATE_RULE,
				"handles new client job",
				"rule runs when new client job was received");
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		final ClientJob job = facts.get(MESSAGE_CONTENT);
		return agentProps.getJobsToBeExecuted().offer(job);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final ClientJob job = facts.get(MESSAGE_CONTENT);
		int newRuleSetIdx = facts.get(RULE_SET_IDX);

		if (nonNull(job.getSelectionPreference())) {
			final String log = "Comparing RMA offers using custom comparator";
			final String ruleSetName = "CUSTOM_CLIENT_COMPARATOR_" + job.getClientIdentifier().toUpperCase();
			final RuleSetRest rules = constructRuleSetForCustomClientComparison(job.getSelectionPreference(),
					ruleSetName, log, job.getJobId(), SCHEDULER);
			newRuleSetIdx = controller.getLatestRuleSetIdx().get() + 1;

			MDC.put(MDC_JOB_ID, job.getJobId());
			MDC.put(MDC_RULE_SET_ID, valueOf(newRuleSetIdx));
			logger.info("Client {} requested to use custom server comparison. Adding rule set {}",
					job.getClientIdentifier(), ruleSetName);

			final RuleSet modifications = new RuleSet(rules);
			controller.addModifiedTemporaryRuleSetFromCurrent(modifications, newRuleSetIdx);
		}
		agentProps.addJob(job, newRuleSetIdx, CREATED);

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_RULE_SET_ID, valueOf(newRuleSetIdx));
		logger.info("Job {} has been successfully added to job scheduling queue", job.getJobId());

		agentNode.updateScheduledJobQueue(agentProps);
		agent.send(prepareJobStatusMessageForClient(job, SCHEDULED_JOB_ID, newRuleSetIdx));
	}

}