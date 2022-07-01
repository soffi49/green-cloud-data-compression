package messages.domain;

import static common.constant.MessageProtocolConstants.DELAYED_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.REFUSE;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import jade.lang.acl.ACLMessage;

/**
 * Class storing methods used in creating reply messages
 */
public class ReplyMessageFactory {

    /**
     * Method prepares the reply message containing the object content
     *
     * @param reply        reply ACLMessage that is to be sent
     * @param responseData object data that is attached as message content
     * @param performative performative of the reply message
     * @return reply ACLMessage
     */
    public static ACLMessage prepareReply(ACLMessage reply, Object responseData, Integer performative) {
        reply.setPerformative(performative);
        try {
            reply.setContent(getMapper().writeValueAsString(responseData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return reply;
    }

    /**
     * Method prepares the reply message containing the simple string content
     *
     * @param reply        reply ACLMessage that is to be sent
     * @param content      string that is attached as message content
     * @param performative performative of the reply message
     * @return reply ACLMessage
     */
    public static ACLMessage prepareStringReply(ACLMessage reply, String content, Integer performative) {
        reply.setPerformative(performative);
        reply.setContent(content);
        return reply;
    }

    /**
     * Method prepares the reply refusal message
     *
     * @param replyMessage reply ACLMessage that is to be sent
     * @return reply ACLMessage
     */
    public static ACLMessage prepareRefuseReply(final ACLMessage replyMessage) {
        replyMessage.setPerformative(REFUSE);
        replyMessage.setContent("REFUSE");
        return replyMessage;
    }

    /**
     * Method prepares the reply message which confirms that the job execution has started
     *
     * @param replyMessage reply ACLMessage that is to be sent
     * @param jobId        unique identifier of the job of interest
     * @return reply ACLMessage
     */
    public static ACLMessage prepareConfirmationReply(final String jobId, final ACLMessage replyMessage) {
        replyMessage.setPerformative(ACLMessage.INFORM);
        replyMessage.setProtocol(STARTED_JOB_PROTOCOL);
        replyMessage.setContent(String.format("The execution of job %s started!", jobId));
        return replyMessage;
    }
}
