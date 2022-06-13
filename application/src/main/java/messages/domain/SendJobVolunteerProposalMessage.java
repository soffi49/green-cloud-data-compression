package messages.domain;

import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import domain.ImmutableServerData;
import domain.job.Job;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

public class SendJobVolunteerProposalMessage {

    private final ACLMessage message;

    private SendJobVolunteerProposalMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobVolunteerProposalMessage create(final ServerAgent serverAgent,
                                                         final double servicePrice,
                                                         final Job job,
                                                         final ACLMessage replyMessage) {
        final ImmutableServerData jobOffer = ImmutableServerData.builder()
                .servicePrice(servicePrice)
                .powerInUse((serverAgent).getPowerInUse())
                .pricePerHour((serverAgent).getPricePerHour())
                .availableCapacity((serverAgent).getAvailableCapacity())
                .job(job)
                .build();
        replyMessage.setPerformative(ACLMessage.PROPOSE);
        try {
            replyMessage.setContent(getMapper().writeValueAsString(jobOffer));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new SendJobVolunteerProposalMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
