package agents.cloudnetwork.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ServerData;
import domain.job.ImmutablePricedJob;
import domain.job.PricedJob;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.Map;

import static jade.core.AID.ISGUID;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static mapper.JsonMapper.getMapper;

public class SendJobOfferMessage {
    private final ACLMessage message;
    private SendJobOfferMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobOfferMessage create(final ServerData server, final ACLMessage replyMessage) {
        final PricedJob pricedJob = ImmutablePricedJob.builder()
                .job(server.getJob())
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
