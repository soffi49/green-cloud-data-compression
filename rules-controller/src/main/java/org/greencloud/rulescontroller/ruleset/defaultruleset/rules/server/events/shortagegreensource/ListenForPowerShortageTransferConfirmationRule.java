package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.events.shortagegreensource;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static jade.lang.acl.MessageTemplate.and;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;
import static org.greencloud.commons.constants.FactTypeConstants.AGENT;
import static org.greencloud.commons.constants.FactTypeConstants.EVENT_TIME;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOBS;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_ID;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_RULE_SET_ID;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.EXECUTING_ON_GREEN;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.ON_HOLD_TRANSFER;
import static org.greencloud.commons.enums.rules.RuleType.HANDLE_POWER_SHORTAGE_TRANSFER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_CONFIRMATION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.TRANSFER_JOB_FOR_GS_IN_RMA_RULE;
import static org.greencloud.commons.mapper.JobMapper.mapClientJobToJobInstanceId;
import static org.greencloud.commons.mapper.JobMapper.mapToPowerShortageJob;
import static org.greencloud.commons.mapper.JsonMapper.getMapper;
import static org.greencloud.commons.utils.job.JobUtils.getJobByInstanceId;
import static org.greencloud.commons.utils.job.JobUtils.isJobStarted;
import static org.greencloud.commons.utils.messaging.constants.MessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static org.greencloud.commons.utils.messaging.constants.MessageContentConstants.TRANSFER_SUCCESSFUL_MESSAGE;
import static org.greencloud.commons.utils.messaging.constants.MessageTemplatesConstants.LISTEN_FOR_SOURCE_TRANSFER_CONFIRMATION;
import static org.greencloud.commons.utils.messaging.factory.ReplyMessageFactory.prepareReply;
import static org.greencloud.commons.utils.messaging.factory.ReplyMessageFactory.prepareStringReply;
import static org.greencloud.commons.utils.time.TimeSimulation.getCurrentTime;
import static org.greencloud.rulescontroller.ruleset.RuleSetSelector.SELECT_BY_FACTS_IDX;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.Optional;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.instance.JobInstanceIdentifier;
import org.greencloud.commons.domain.job.transfer.JobDivided;
import org.greencloud.commons.domain.job.transfer.JobPowerShortageTransfer;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.domain.CompressedDataSent;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.initiate.InitiateRequest;
import org.greencloud.rulescontroller.behaviour.schedule.ScheduleOnce;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentSingleMessageListenerRule;
import org.jeasy.rules.api.Facts;
import org.slf4j.Logger;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ListenForPowerShortageTransferConfirmationRule
		extends AgentSingleMessageListenerRule<ServerAgentProps, ServerNode> {

	private static final Logger logger = getLogger(ListenForPowerShortageTransferConfirmationRule.class);
	private static final long TRANSFER_EXPIRATION_TIME = 3000L;

	public ListenForPowerShortageTransferConfirmationRule(
			final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(LISTEN_FOR_JOB_TRANSFER_CONFIRMATION_RULE,
				"listen for confirmation of job transfer in Green Source",
				"rule listens for the confirmation of the job transfer between owned Green Sources");
	}

	@Override
	protected MessageTemplate constructMessageTemplate(final RuleSetFacts facts) {
		try {
			final ClientJob job = facts.get(JOB);
			final String expectedContent = getMapper().writeValueAsString(mapClientJobToJobInstanceId(job));
			return and(MatchContent(expectedContent), LISTEN_FOR_SOURCE_TRANSFER_CONFIRMATION);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected long specifyExpirationTime(final RuleSetFacts facts) {
		return TRANSFER_EXPIRATION_TIME;
	}

	@Override
	protected void handleMessageProcessing(final ACLMessage message, final RuleSetFacts facts) {
		final JobDivided<ClientJob> newJobInstances = facts.get(JOBS);
		final String jobId = newJobInstances.getSecondInstance().getJobId();

		MDC.put(MDC_JOB_ID, jobId);
		MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));
		switch (message.getPerformative()) {
			case INFORM -> handleJobTransfer(message, facts);
			default -> handleTransferFailure(jobId, facts);
		}
	}

	private void handleJobTransfer(final ACLMessage inform, final Facts facts) {
		final ACLMessage gsRequest = facts.get(MESSAGE);
		final JobDivided<ClientJob> newJobInstances = facts.get(JOBS);
		final ClientJob job = getJobByInstanceId(newJobInstances.getSecondInstance().getJobInstanceId(),
				agentProps.getServerJobs());

		if (nonNull(job)) {
			MDC.put(MDC_JOB_ID, job.getJobId());
			MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));
			logger.info("Scheduling the job {} transfer. Sending confirmation to green source", job.getJobId());
			agent.send(prepareReply(gsRequest, TRANSFER_SUCCESSFUL_MESSAGE, INFORM));
			updateJobStatus(job);

			final RuleSetFacts transferFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));
			transferFacts.put(JOB, newJobInstances.getSecondInstance());
			transferFacts.put(JOBS, newJobInstances);
			transferFacts.put(AGENT, inform.getSender());

			agent.addBehaviour(
					ScheduleOnce.create(agent, transferFacts, HANDLE_POWER_SHORTAGE_TRANSFER_RULE, controller,
							SELECT_BY_FACTS_IDX));
		} else {
			logger.info("Job execution finished before transfer");
			agent.send(prepareStringReply(gsRequest, JOB_NOT_FOUND_CAUSE_MESSAGE, REFUSE));
		}
	}

	private void handleTransferFailure(final String jobId, final Facts facts) {
		final JobDivided<ClientJob> newJobInstances = facts.get(JOBS);
		final ACLMessage gsRequest = facts.get(MESSAGE);
		final Instant shortageStart = facts.get(EVENT_TIME);

		MDC.put(MDC_JOB_ID, jobId);
		MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));
		logger.info("Job {} transfer has failed in green source. Passing transfer request to Regional Manager", jobId);
		final ClientJob job = newJobInstances.getSecondInstance();
		final JobInstanceIdentifier jobInstance = mapClientJobToJobInstanceId(job);
		final Optional<CompressedDataSent> compressedData = agentProps.compressDataForTransfer(job);
		final JobPowerShortageTransfer jobTransfer = compressedData
				.map(data -> mapToPowerShortageJob(jobInstance, shortageStart, data))
				.orElse(mapToPowerShortageJob(jobInstance, shortageStart));

		final RuleSetFacts rmaTransferFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));
		rmaTransferFacts.put(JOB, jobTransfer);
		rmaTransferFacts.put(JOB_ID, jobInstance);
		rmaTransferFacts.put(MESSAGE, gsRequest);

		agent.addBehaviour(
				InitiateRequest.create(agent, rmaTransferFacts, TRANSFER_JOB_FOR_GS_IN_RMA_RULE, controller));
	}

	private void updateJobStatus(final ClientJob jobToExecute) {
		final boolean isJobRunning = agentProps.getServerJobs().get(jobToExecute).equals(ON_HOLD_TRANSFER);

		if (!isJobStarted(jobToExecute, agentProps.getServerJobs())) {
			final JobExecutionStatusEnum prevStatus = agentProps.getServerJobs().get(jobToExecute);
			final JobExecutionStatusEnum newStatus = EXECUTING_ON_GREEN.getStatus(isJobRunning);

			agentProps.getJobsExecutionTime()
					.updateJobExecutionDuration(jobToExecute, prevStatus, newStatus, getCurrentTime());
		}
		agentProps.getServerJobs().replace(jobToExecute, EXECUTING_ON_GREEN.getStatus(isJobRunning));
	}
}
