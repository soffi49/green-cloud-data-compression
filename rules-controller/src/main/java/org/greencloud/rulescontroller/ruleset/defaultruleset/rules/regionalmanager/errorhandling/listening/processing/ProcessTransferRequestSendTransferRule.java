package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.listening.processing;

import static java.lang.String.valueOf;
import static org.greencloud.commons.constants.FactTypeConstants.AGENTS;
import static org.greencloud.commons.constants.FactTypeConstants.EVENT_TIME;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_COMPRESSION;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_RULE_SET_ID;
import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.COMPRESSION;
import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.DATA;
import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.INPUT;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_SEND_TRANSFER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_TRANSFER_REQUEST_RULE;
import static org.greencloud.commons.mapper.JobMapper.mapToJobNewStartTime;
import static org.greencloud.commons.mapper.ResourceMapper.mapToResourceWithNewCharacteristic;
import static org.greencloud.commons.utils.time.TimeScheduler.alignStartTimeToSelectedTime;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.basic.ImmutableClientJob;
import org.greencloud.commons.domain.resources.ImmutableResourceCharacteristic;
import org.greencloud.commons.domain.resources.Resource;
import org.greencloud.commons.domain.resources.ResourceCharacteristic;
import org.greencloud.domain.CompressedDataSent;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.initiate.InitiateCallForProposal;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

import jade.core.AID;

public class ProcessTransferRequestSendTransferRule
		extends AgentBasicRule<RegionalManagerAgentProps, RegionalManagerNode> {

	private static final Logger logger = getLogger(ProcessTransferRequestSendTransferRule.class);

	protected ProcessTransferRequestSendTransferRule(
			final RulesController<RegionalManagerAgentProps, RegionalManagerNode> rulesController) {
		super(rulesController, 1);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_RULE,
				LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_SEND_TRANSFER_RULE,
				"transfer job handler - send transfer",
				"handles transferring job from one Server to another");
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		final List<AID> agents = facts.get(AGENTS);
		return !agents.isEmpty();
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final ClientJob job = facts.get(JOB);
		final Instant failureStart = facts.get(EVENT_TIME);
		final Optional<CompressedDataSent> jobCompressionData = facts.get(JOB_COMPRESSION);

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));

		logger.info("Sending call for proposal to Server Agents to transfer job with id {}", job.getJobId());

		final Instant newJobStartTime = alignStartTimeToSelectedTime(job.getStartTime(), failureStart);
		ClientJob jobToTransfer = mapToJobNewStartTime(job, newJobStartTime);

		if (jobCompressionData.isPresent()) {
			final ResourceCharacteristic compressionInfoCharacteristic = ImmutableResourceCharacteristic.builder()
					.value(jobCompressionData.get())
					.isRequired(false)
					.build();
			final Resource updatedResource = mapToResourceWithNewCharacteristic(
					jobToTransfer.getRequiredResources().get(INPUT),
					compressionInfoCharacteristic,
					COMPRESSION);
			final Map<String, Resource> newResources = new HashMap<>(job.getRequiredResources());
			newResources.replace(INPUT, updatedResource);

			jobToTransfer = ImmutableClientJob.copyOf(jobToTransfer).withRequiredResources(newResources);
		}

		facts.put(JOB, jobToTransfer);
		facts.put(JOB_COMPRESSION, facts.get(JOB_COMPRESSION));

		agent.addBehaviour(InitiateCallForProposal.create(agent, facts, PROCESS_TRANSFER_REQUEST_RULE, controller));
	}
}
