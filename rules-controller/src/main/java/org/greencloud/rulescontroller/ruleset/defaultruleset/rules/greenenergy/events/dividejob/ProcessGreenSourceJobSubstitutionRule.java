package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.greenenergy.events.dividejob;

import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_IS_STARTED;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_PREVIOUS;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.enums.rules.RuleType.JOB_MANUAL_FINISH_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_JOB_SUBSTITUTION_RULE;
import static org.greencloud.rulescontroller.ruleset.RuleSetSelector.SELECT_BY_FACTS_IDX;

import java.util.concurrent.ConcurrentMap;

import org.greencloud.commons.args.agent.greenenergy.agent.GreenEnergyAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ServerJob;
import org.greencloud.commons.domain.job.extended.JobStatusWithTime;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.gui.agents.greenenergy.GreenEnergyNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.schedule.ScheduleOnce;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;

public class ProcessGreenSourceJobSubstitutionRule extends AgentBasicRule<GreenEnergyAgentProps, GreenEnergyNode> {

	public ProcessGreenSourceJobSubstitutionRule(
			final RulesController<GreenEnergyAgentProps, GreenEnergyNode> rulesController) {
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
		final ServerJob newJobInstance = facts.get(JOB);
		final boolean hasStarted = facts.get(JOB_IS_STARTED);
		final RuleSetFacts jobManualFinish = new RuleSetFacts(facts.get(RULE_SET_IDX));
		final ServerJob previousJob = facts.get(JOB_PREVIOUS);

		if(hasStarted) {
			final ConcurrentMap<JobExecutionStatusEnum, JobStatusWithTime> prevExecutionTime =
					agentProps.getJobsExecutionTime().getForJob(previousJob);
			agentProps.getJobsExecutionTime().addDurationMap(newJobInstance, prevExecutionTime);
		} else {
			agentProps.getJobsExecutionTime().addDurationMap(newJobInstance);
		}

		final Double jobPrice = agentProps.getPriceForJob().get(previousJob);
		agentProps.getPriceForJob().put(newJobInstance, jobPrice);
		agentProps.getPriceForJob().remove(previousJob);
		agentProps.getJobsExecutionTime().removeDurationMap(previousJob);

		jobManualFinish.put(JOB, newJobInstance);
		agent.addBehaviour(
				ScheduleOnce.create(agent, jobManualFinish, JOB_MANUAL_FINISH_RULE, controller, SELECT_BY_FACTS_IDX));
	}
}
