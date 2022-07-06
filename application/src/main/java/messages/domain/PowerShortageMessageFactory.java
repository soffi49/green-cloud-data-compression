package messages.domain;

import static common.constant.MessageProtocolConstants.*;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.ImmutableJobTransfer;
import domain.job.JobTransfer;
import domain.job.PowerShortageTransfer;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.time.OffsetDateTime;

public class PowerShortageMessageFactory {

    /**
     * Method prepares the message informing about the shortage in power for the given green source
     *
     * @param powerShortageTransfer content of the message consisting of the list of jobs to transfer and power shortage time
     * @param serverAddress         server address
     * @return inform ACLMessage
     */
    public static ACLMessage preparePowerShortageInformation(final PowerShortageTransfer powerShortageTransfer, final AID serverAddress) {
        final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        try {
            message.setContent(getMapper().writeValueAsString(powerShortageTransfer));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        message.setProtocol(POWER_SHORTAGE_ALERT_PROTOCOL);
        message.addReceiver(serverAddress);
        return message;
    }

    /**
     * Method prepares the information message about confirmed job transfer
     *
     * @param jobId        unique identifier of the job
     * @param shortageTime time when the power shortage will start
     * @param receiver     address of an agent which announced the power shortage
     * @param protocol     message protocol
     * @return inform ACLMessage
     */
    public static ACLMessage prepareTransferInformation(final String jobId, final OffsetDateTime shortageTime, final AID receiver, final String protocol) {
        final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        final JobTransfer jobTransfer = ImmutableJobTransfer.builder().jobId(jobId).transferTime(shortageTime).build();
        try {
            message.setContent(getMapper().writeValueAsString(jobTransfer));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        message.setProtocol(protocol);
        message.addReceiver(receiver);
        return message;
    }

    /**
     * Method prepares the message requesting from the given agent to cancel the job transfer
     *
     * @param jobTransfer job transfer that is to be cancelled
     * @return request ACLMessage
     */
    public static ACLMessage prepareTransferCancellationRequest(final JobTransfer jobTransfer, final AID receiver) {
        final ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        try {
            message.setContent(getMapper().writeValueAsString(jobTransfer));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        message.setProtocol(CANCELLED_TRANSFER_PROTOCOL);
        message.addReceiver(receiver);
        return message;
    }
}
