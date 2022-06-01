package agents.server.message;

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
        replyMessage.setPerformative(ACLMessage.PROPOSE);
        try {
            final ImmutableServerData data = ImmutableServerData.builder()
                .servicePrice(servicePrice)
                .powerInUse((serverAgent).getPowerInUse())
                .pricePerHour((serverAgent).getPricePerHour())
                .availableCapacity((serverAgent).getAvailableCapacity())
                .job(job)
                .build();
            replyMessage.setContent(getMapper().writeValueAsString(data));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new SendJobVolunteerProposalMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
