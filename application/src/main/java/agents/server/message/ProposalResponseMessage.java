package agents.server.message;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.server.ServerAgent;
import domain.ImmutableServerData;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

import static mapper.JsonMapper.getMapper;

public class ProposalResponseMessage {

    private final ACLMessage message;

    private ProposalResponseMessage(ACLMessage message) {
        this.message = message;
    }

    public static ProposalResponseMessage create(final ServerAgent serverAgent) {
        final ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
        try {
            final ImmutableServerData data = ImmutableServerData.builder()
                    .powerInUse((serverAgent).getPowerInUse())
                    .pricePerHour((serverAgent).getPricePerHour())
                    .availableCapacity((serverAgent).getAvailableCapacity())
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
