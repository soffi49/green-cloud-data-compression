package messages.domain;

import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

import domain.job.JobInstanceIdentifier;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PowerShortageMessageFactory {

	/**
	 * Method prepares the message containing the request regarding job transfer
	 *
	 * @param powerShortageJob content of the message consisting of the job to transfer and power shortage time
	 * @param receiver         receivers of the message
	 * @return request ACLMessage
	 */
	public static ACLMessage preparePowerShortageTransferRequest(final PowerShortageJob powerShortageJob,
			final AID receiver) {
		final ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
		try {
			message.setContent(getMapper().writeValueAsString(powerShortageJob));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		message.setProtocol(POWER_SHORTAGE_ALERT_PROTOCOL);
		message.addReceiver(receiver);
		return message;
	}

	/**
	 * Method prepares the message informing about the finish of the shortage in power for given agent
	 *
	 * @param jobInstanceId unique identifier of the job instance
	 * @param receiver      message receiver
	 * @return inform ACLMessage
	 */
	public static ACLMessage preparePowerShortageFinishInformation(final JobInstanceIdentifier jobInstanceId,
			final AID receiver) {
		final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		try {
			message.setContent(getMapper().writeValueAsString(jobInstanceId));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		message.setProtocol(POWER_SHORTAGE_FINISH_ALERT_PROTOCOL);
		message.addReceiver(receiver);
		return message;
	}

	/**
	 * Method prepares the message passing the job affected by the power shortage with provided protocol
	 *
	 * @param powerShortageJob message content
	 * @param receiver         address of a receiver agent
	 * @param protocol         message protocol
	 * @return inform ACLMessage
	 */
	public static ACLMessage prepareJobPowerShortageInformation(final PowerShortageJob powerShortageJob,
			final AID receiver,
			final String protocol) {
		final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
		try {
			message.setContent(getMapper().writeValueAsString(powerShortageJob));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		message.setProtocol(protocol);
		message.addReceiver(receiver);
		return message;
	}
}
