package org.greencloud.commons.utils.messaging.factory;

import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;

import org.greencloud.commons.domain.job.instance.JobInstanceIdentifier;
import org.greencloud.commons.mapper.JobMapper;
import org.greencloud.commons.utils.messaging.constants.MessageProtocolConstants;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.transfer.JobPowerShortageTransfer;
import org.greencloud.commons.utils.messaging.MessageBuilder;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * Class storing methods used in creating messages for communicating the network errors
 */
public class NetworkErrorMessageFactory {

	/**
	 * Method prepares the message containing the request regarding job transfer
	 *
	 * @param powerShortageJob content of the message consisting of the job to transfer and power shortage time
	 * @param receiver         receivers of the message
	 * @return REQUEST ACLMessage
	 */
	public static ACLMessage prepareJobTransferRequest(final JobPowerShortageTransfer powerShortageJob,
			final AID receiver, final Integer ruleSet) {
		return MessageBuilder.builder(ruleSet)
				.withPerformative(REQUEST)
				.withObjectContent(powerShortageJob)
				.withMessageProtocol(MessageProtocolConstants.NETWORK_ERROR_ALERT_PROTOCOL)
				.withReceivers(receiver)
				.build();
	}

	/**
	 * Method prepares the message containing the request regarding job green power re-supply
	 *
	 * @param job      job affected by source power shortage to be supplied again using green power
	 * @param receiver receiver of the message
	 * @return REQUEST ACLMessage
	 */
	public static ACLMessage prepareGreenPowerSupplyRequest(final ClientJob job, final AID receiver,
			final Integer ruleSet) {
		return MessageBuilder.builder(ruleSet)
				.withPerformative(REQUEST)
				.withObjectContent(JobMapper.mapClientJobToJobInstanceId(job))
				.withMessageProtocol(MessageProtocolConstants.SERVER_POWER_SHORTAGE_RE_SUPPLY_PROTOCOL)
				.withReceivers(receiver)
				.build();
	}

	/**
	 * Method prepares the message about the job transfer update that is sent to scheduler
	 *
	 * @param jobInstanceId unique job instance
	 * @param regionalManager  Regional Manager to which message is sent
	 * @param protocol      protocol used in transfer messages
	 * @return INFORM ACLMessage
	 */
	public static ACLMessage prepareJobTransferUpdateMessageForRMA(final JobInstanceIdentifier jobInstanceId,
			final String protocol, final AID regionalManager, final Integer ruleSet) {
		final int performative = protocol.equals(MessageProtocolConstants.FAILED_TRANSFER_PROTOCOL) ? FAILURE : INFORM;
		return MessageBuilder.builder(ruleSet)
				.withObjectContent(jobInstanceId)
				.withPerformative(performative)
				.withReceivers(regionalManager)
				.withMessageProtocol(protocol)
				.build();
	}

	/**
	 * Method prepares the message passing the job affected by some error with provided protocol
	 *
	 * @param messageContent message content
	 * @param receivers      address of a receiver agents
	 * @param protocol       message protocol
	 * @return INFORM ACLMessage
	 */
	public static ACLMessage prepareNetworkFailureInformation(final Object messageContent, final String protocol,
			final Integer ruleSet, final AID... receivers) {
		return MessageBuilder.builder(ruleSet)
				.withPerformative(INFORM)
				.withMessageProtocol(protocol)
				.withObjectContent(messageContent)
				.withReceivers(receivers)
				.build();
	}
}
