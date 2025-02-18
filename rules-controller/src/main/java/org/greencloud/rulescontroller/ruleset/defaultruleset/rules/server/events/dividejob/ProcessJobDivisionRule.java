package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.events.dividejob;

import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_DIVIDED;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_FINISH_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_PREVIOUS;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_START_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.ACCEPTED;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_JOB_DIVISION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.START_JOB_EXECUTION_RULE;
import static org.greencloud.commons.utils.job.JobUtils.isJobStarted;
import static org.greencloud.rulescontroller.ruleset.RuleSetSelector.SELECT_BY_FACTS_IDX;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.extended.JobStatusWithTime;
import org.greencloud.commons.domain.job.transfer.JobDivided;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.commons.mapper.JobMapper;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.schedule.ScheduleOnce;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;

public class ProcessJobDivisionRule extends AgentBasicRule<ServerAgentProps, ServerNode> {

	public ProcessJobDivisionRule(final RulesController<ServerAgentProps, ServerNode> rulesController) {
		super(rulesController);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(PROCESS_JOB_DIVISION_RULE,
				"divides job instances into 2",
				"rule produces 2 job instances and initiates their execution");
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final JobDivided<ClientJob> jobInstances = facts.get(JOB_DIVIDED);
		final ClientJob affectedJob = jobInstances.getSecondInstance();
		final ClientJob nonAffectedJob = jobInstances.getFirstInstance();
		final ClientJob prevJob = facts.get(JOB_PREVIOUS);
		updateJobExecutionData(prevJob, nonAffectedJob, affectedJob);

		final RuleSetFacts jobStartFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));

		jobStartFacts.put(RULE_TYPE, START_JOB_EXECUTION_RULE);
		jobStartFacts.put(JOB, affectedJob);
		jobStartFacts.put(JOB_START_INFORM, false);
		jobStartFacts.put(JOB_FINISH_INFORM, true);

		agent.addBehaviour(
				ScheduleOnce.create(agent, jobStartFacts, START_JOB_EXECUTION_RULE, controller, SELECT_BY_FACTS_IDX));
		agentProps.incrementJobCounter(JobMapper.mapClientJobToJobInstanceId(affectedJob), ACCEPTED);

		final RuleSetFacts jobFinishFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));

		jobFinishFacts.put(RULE_TYPE, FINISH_JOB_EXECUTION_RULE);
		jobFinishFacts.put(JOB, nonAffectedJob);
		jobFinishFacts.put(JOB_FINISH_INFORM, false);

		agent.addBehaviour(
				ScheduleOnce.create(agent, jobFinishFacts, FINISH_JOB_EXECUTION_RULE, controller, SELECT_BY_FACTS_IDX));

		if (!isJobStarted(nonAffectedJob, agentProps.getServerJobs())) {
			agentProps.getJobsExecutionTime().addDurationMap(nonAffectedJob);

			final RuleSetFacts jobStartFactsNonAffected = new RuleSetFacts(facts.get(RULE_SET_IDX));

			jobStartFactsNonAffected.put(RULE_TYPE, START_JOB_EXECUTION_RULE);
			jobStartFactsNonAffected.put(JOB, nonAffectedJob);
			jobStartFactsNonAffected.put(JOB_START_INFORM, true);
			jobStartFactsNonAffected.put(JOB_FINISH_INFORM, false);

			agent.addBehaviour(
					ScheduleOnce.create(agent, jobStartFactsNonAffected, START_JOB_EXECUTION_RULE, controller,
							SELECT_BY_FACTS_IDX));
		}
	}

	private void updateJobExecutionData(final ClientJob prevJob, final ClientJob nonAffectedJob,
			final ClientJob affectedJob) {
		final Double jobPrice = agentProps.getServerPriceForJob().get(prevJob.getJobInstanceId());

		agentProps.getServerPriceForJob().put(affectedJob.getJobInstanceId(), jobPrice);
		agentProps.getServerPriceForJob().put(nonAffectedJob.getJobInstanceId(), jobPrice);
		agentProps.getJobsExecutionTime().addDurationMap(affectedJob);

		if (isJobStarted(nonAffectedJob, agentProps.getServerJobs())) {
			final ConcurrentMap<JobExecutionStatusEnum, JobStatusWithTime> prevExecutionTime =
					new ConcurrentHashMap<>(agentProps.getJobsExecutionTime().getForJob(prevJob));
			agentProps.getJobsExecutionTime().addDurationMap(nonAffectedJob, prevExecutionTime);
		} else {
			agentProps.getJobsExecutionTime().addDurationMap(nonAffectedJob);
		}

		agentProps.getServerPriceForJob().remove(prevJob.getJobInstanceId());
		agentProps.getJobsExecutionTime().removeDurationMap(prevJob);
		agentProps.getEnergyExecutionCost().remove(prevJob.getJobInstanceId());
	}
}
