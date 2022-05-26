package agents.server.message;

import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import domain.ImmutableServerData;
import domain.job.Job;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

public class ProposalResponseMessage {

    private final ACLMessage message;

    private ProposalResponseMessage(ACLMessage message) {
        this.message = message;
    }

    public static ProposalResponseMessage create(final ServerAgent serverAgent, final double servicePrice, Job job) {
        final ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
        try {
            final ImmutableServerData data = ImmutableServerData.builder()
                .servicePrice(servicePrice)
                .powerInUse((serverAgent).getPowerInUse())
                .pricePerHour((serverAgent).getPricePerHour())
                .availableCapacity((serverAgent).getAvailableCapacity())
                .job(job)
                .build();
            response.setContent(getMapper().writeValueAsString(data));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        response.addReceiver(serverAgent.getOwnerCloudNetworkAgent());
        return new ProposalResponseMessage(response);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
