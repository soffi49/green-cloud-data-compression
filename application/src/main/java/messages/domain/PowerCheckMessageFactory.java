package messages.domain;

import static jade.lang.acl.ACLMessage.REQUEST;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.CheckedPowerJob;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PowerCheckMessageFactory {

    /**
     * Method prepares the message to initiate the power checking
     *
     * @param job            job for which available power have to be verified before the actual job execution
     * @param conversationId conversation id of the message
     * @param protocol       protocol for the message
     * @param receiver       receiver of the message
     * @return {@link ACLMessage} with REQUEST performative
     */
    public static ACLMessage preparePowerCheckMessage(final CheckedPowerJob job, final String conversationId,
                                                      String protocol, AID receiver) {
        final ACLMessage request = new ACLMessage(REQUEST);
        request.setProtocol(protocol);
        request.setConversationId(conversationId);
        try {
            request.setContent(getMapper().writeValueAsString(job));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        request.addReceiver(receiver);
        return request;
    }
}