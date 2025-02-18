package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.events.dividejob;

import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_FINISH_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_IS_STARTED;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_PREVIOUS;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_START_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_JOB_SUBSTITUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.START_JOB_EXECUTION_RULE;
import static org.greencloud.rulescontroller.ruleset.RuleSetSelector.SELECT_BY_FACTS_IDX;

import java.util.concurrent.ConcurrentMap;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.extended.JobStatusWithTime;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.schedule.ScheduleOnce;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;

public class ProcessJobSubstitutionRule extends AgentBasicRule<ServerAgentProps, ServerNode> {

	public ProcessJobSubstitutionRule(final RulesController<ServerAgentProps, ServerNode> rulesController) {
		super(rulesController);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(PROCESS_JOB_SUBSTITUTION_RULE,
				"substituting job instances with new ones",
				"rule substitutes old job instances with new ones");
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final boolean hasStarted = facts.get(JOB_IS_STARTED);
		final ClientJob newJobInstance = facts.get(JOB);
		final ClientJob prevJob = facts.get(JOB_PREVIOUS);
		updateJobExecutionData(prevJob, newJobInstance);

		if (hasStarted) {
			final RuleSetFacts jobFinishFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));
			final ConcurrentMap<JobExecutionStatusEnum, JobStatusWithTime> prevExecutionTime =
					agentProps.getJobsExecutionTime().getForJob(prevJob);
			agentProps.getJobsExecutionTime().addDurationMap(newJobInstance, prevExecutionTime);

			jobFinishFacts.put(RULE_TYPE, FINISH_JOB_EXECUTION_RULE);
			jobFinishFacts.put(JOB, newJobInstance);
			jobFinishFacts.put(JOB_FINISH_INFORM, true);

			agent.addBehaviour(ScheduleOnce.create(agent, jobFinishFacts, FINISH_JOB_EXECUTION_RULE, controller,
					SELECT_BY_FACTS_IDX));
		} else {
			final RuleSetFacts jobStartFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));
			agentProps.getJobsExecutionTime().addDurationMap(newJobInstance);

			jobStartFacts.put(RULE_TYPE, START_JOB_EXECUTION_RULE);
			jobStartFacts.put(JOB, newJobInstance);
			jobStartFacts.put(JOB_START_INFORM, true);
			jobStartFacts.put(JOB_FINISH_INFORM, true);

			agent.addBehaviour(ScheduleOnce.create(agent, jobStartFacts, START_JOB_EXECUTION_RULE, controller,
					SELECT_BY_FACTS_IDX));
		}
		agentProps.getEnergyExecutionCost().remove(prevJob.getJobInstanceId());
		agentProps.getJobsExecutionTime().removeDurationMap(prevJob);
	}

	private void updateJobExecutionData(final ClientJob prevJob, final ClientJob newJobInstance) {
		final Double jobPrice = agentProps.getServerPriceForJob().get(prevJob.getJobInstanceId());

		agentProps.getServerPriceForJob().put(newJobInstance.getJobInstanceId(), jobPrice);
		agentProps.getServerPriceForJob().remove(prevJob.getJobInstanceId());
	}
}
