package messages.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import jade.lang.acl.ACLMessage;

import static mapper.JsonMapper.getMapper;

public class SendJobOfferResponseMessage {
    private final ACLMessage message;

    private SendJobOfferResponseMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobOfferResponseMessage create(final Job job, final int replyPerformative, final ACLMessage replyMessage) {
        replyMessage.setPerformative(replyPerformative);
        try {
            replyMessage.setContent(getMapper().writeValueAsString(job));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new SendJobOfferResponseMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
