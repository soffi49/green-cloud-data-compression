package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.job.listening.newjob;

import static java.time.Duration.between;
import static java.time.Instant.now;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.RESOURCES;
import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.COMPRESSION;
import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.INPUT;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.mapper.JsonMapper.getMapper;
import static org.greencloud.rulescontroller.rule.combined.domain.AgentCombinedRuleType.EXECUTE_FIRST;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.resources.Resource;
import org.greencloud.domain.CompressedDataSent;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.gui.messages.domain.ExchangeMessageData;
import org.greencloud.gui.messages.domain.ImmutableExchangeMessageData;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.combined.AgentCombinedRule;
import org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.job.listening.newjob.processing.ProcessRMANewJobNoGreenSourcesRule;
import org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.job.listening.newjob.processing.ProcessRMANewJobNoResourcesRule;
import org.greencloud.rulescontroller.ruleset.defaultruleset.rules.server.job.listening.newjob.processing.ProcessRMANewJobSuccessfullyRule;

public class ProcessRMANewJobCombinedRule extends AgentCombinedRule<ServerAgentProps, ServerNode> {

	public ProcessRMANewJobCombinedRule(final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller, EXECUTE_FIRST);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_RECEIVER_HANDLER_RULE,
				"handles new RMA job request",
				"handling new job sent by RMA");
	}

	/**
	 * Method construct set of rules that are to be combined
	 */
	@Override
	protected List<AgentRule> constructRules() {
		return List.of(
				new ProcessRMANewJobNoGreenSourcesRule(controller),
				new ProcessRMANewJobNoResourcesRule(controller),
				new ProcessRMANewJobSuccessfullyRule(controller)
		);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		ClientJob job = facts.get(MESSAGE_CONTENT);
		final Map<String, Resource> resources = agentProps.getAvailableResources(job, null, null);

		if (job.getRequiredResources().containsKey(INPUT) &&
				job.getRequiredResources().get(INPUT).getCharacteristics().containsKey(COMPRESSION)) {
			final CompressedDataSent compressedData = getMapper().convertValue(job.getRequiredResources().get(INPUT)
					.getCharacteristics().get(COMPRESSION).getValue(), CompressedDataSent.class);
			final byte[] inputData = compressedData.getInputData();

			job = job.addInputDataToJobResources(inputData);
			agentNode.addDataAboutMessageExchange(decompressDataTransfer(compressedData, inputData));
		}

		facts.put(JOB, job);
		facts.put(RESOURCES, resources);
	}

	private ExchangeMessageData decompressDataTransfer(final CompressedDataSent compressedDataInfo,
			final byte[] receivedData) {
		final Pair<byte[], Long> dataDecompression = agentProps.decompressDataTransfer(compressedDataInfo,
				receivedData);
		final byte[] decompressedData = dataDecompression.getLeft();
		final double byteRatio = (double) decompressedData.length / compressedDataInfo.getInputDataLength();
		final double compressionRatio =
				(double) compressedDataInfo.getInputDataLength() / compressedDataInfo.getInputData().length;
		final long duration = between(compressedDataInfo.getDataSentTime(), now()).toMillis();

		return ImmutableExchangeMessageData.builder()
				.compressionMethod(compressedDataInfo.getCompressionMethod())
				.bytesSentToBytesReceived(byteRatio)
				.decompressionTime(dataDecompression.getRight())
				.compressionTime(compressedDataInfo.getCompressionDuration())
				.compressionRatio(compressionRatio)
				.transferredSize((long) compressedDataInfo.getInputData().length)
				.estimatedTransferCost(0L)
				.messageRetrievalDuration(duration)
				.build();
	}
}
