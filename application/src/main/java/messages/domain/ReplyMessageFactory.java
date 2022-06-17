package messages.domain;

import static jade.lang.acl.ACLMessage.REFUSE;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import jade.lang.acl.ACLMessage;

public class ReplyMessageFactory {
    public static ACLMessage prepareReply(ACLMessage reply, Object responseData, Integer performative) {
        reply.setPerformative(performative);
        try {
            reply.setContent(getMapper().writeValueAsString(responseData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return reply;
    }

    public static ACLMessage prepareStringReply(ACLMessage reply, String content, Integer performative) {
        reply.setPerformative(performative);
        reply.setContent(content);
        return reply;
    }

    public static ACLMessage prepareRefuseReply(final ACLMessage replyMessage) {
        replyMessage.setPerformative(REFUSE);
        replyMessage.setContent("REFUSE");
        return replyMessage;
    }
}
