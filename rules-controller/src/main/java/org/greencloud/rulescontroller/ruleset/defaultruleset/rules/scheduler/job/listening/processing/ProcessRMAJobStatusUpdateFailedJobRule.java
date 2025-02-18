package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.scheduler.job.listening.processing;

import static java.lang.String.valueOf;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_RULE_SET_ID;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLE_FAILED_JOB_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.FAILED_JOB_ID;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.preparePostponeJobMessageForClient;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.greencloud.commons.args.agent.scheduler.agent.SchedulerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.extended.JobWithStatus;
import org.greencloud.gui.agents.scheduler.SchedulerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.greencloud.rulescontroller.ruleset.defaultruleset.rules.scheduler.job.announcing.domain.AnnouncingConstants;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class ProcessRMAJobStatusUpdateFailedJobRule extends AgentBasicRule<SchedulerAgentProps, SchedulerNode> {

	private static final Logger logger = getLogger(ProcessRMAJobStatusUpdateFailedJobRule.class);

	public ProcessRMAJobStatusUpdateFailedJobRule(
			final RulesController<SchedulerAgentProps, SchedulerNode> controller) {
		super(controller, 3);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(JOB_STATUS_RECEIVER_HANDLER_RULE, JOB_STATUS_RECEIVER_HANDLE_FAILED_JOB_RULE,
				"handles job update - failed job",
				"rule runs when execution of new client job has failed");
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		final String type = facts.get(MESSAGE_TYPE);
		return type.equals(FAILED_JOB_ID);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final Optional<ClientJob> jobOptional = facts.get(JOB);

		if (jobOptional.isPresent()) {
			final ClientJob job = jobOptional.get();
			final JobWithStatus jobStatusUpdate = facts.get(MESSAGE_CONTENT);

			MDC.put(MDC_JOB_ID, job.getJobId());
			MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));

			if (agentProps.postponeJobExecution(job, AnnouncingConstants.JOB_RETRY_MINUTES_ADJUSTMENT)) {
				logger.info("Execution of the job {} has failed. Retrying to execute the job", job.getJobId());
				agent.send(preparePostponeJobMessageForClient(job, facts.get(RULE_SET_IDX)));
			} else {
				logger.info("Execution of the job {} has failed. Passing information to client.", job.getJobId());

				final int ruleSetIdx = agentProps.removeJob(job);
				controller.removeRuleSet(agentProps.getRuleSetForJob(), ruleSetIdx);
				agentProps.getRmaForJobMap().remove(job.getJobId());

				agent.send(
						prepareJobStatusMessageForClient(job, jobStatusUpdate, FAILED_JOB_ID, facts.get(RULE_SET_IDX)));
			}
		}
	}
}
