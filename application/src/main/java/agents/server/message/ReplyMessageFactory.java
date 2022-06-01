package agents.server.message;

import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import jade.lang.acl.ACLMessage;

public class ReplyMessageFactory {

    public static ACLMessage prepareReply(ACLMessage message, Object responseData, Integer performative) {
        var reply = message.createReply();
        reply.setPerformative(performative);
        try {
            reply.setContent(getMapper().writeValueAsString(responseData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return reply;
    }
}
