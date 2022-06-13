package messages.domain;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ServerData;
import domain.job.ImmutablePricedJob;
import domain.job.PricedJob;
import jade.lang.acl.ACLMessage;

public class SendJobOfferMessage {
    private final ACLMessage message;

    private SendJobOfferMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobOfferMessage create(final ServerData server, final ACLMessage replyMessage) {
        final PricedJob pricedJob = ImmutablePricedJob.builder()
                .jobId(server.getJob().getJobId())
                .priceForJob(server.getServicePrice())
                .build();
        replyMessage.setPerformative(PROPOSE);
        try {
            replyMessage.setContent(getMapper().writeValueAsString(pricedJob));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new SendJobOfferMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
