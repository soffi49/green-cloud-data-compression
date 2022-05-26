package agents.server.message;

import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

import agents.server.ServerAgent;
import domain.GreenSourceData;
import jade.lang.acl.ACLMessage;

public class RefuseProposalMessage {

    private final ACLMessage message;

    private RefuseProposalMessage(ACLMessage message) {
        this.message = message;
    }

    public static RefuseProposalMessage create(final ServerAgent serverAgent, final String clientId) {
        final ACLMessage message = new ACLMessage(REFUSE);
        message.setContent("Refuse");
        message.setConversationId(clientId);
        message.addReceiver(serverAgent.getOwnerCloudNetworkAgent());
        return new RefuseProposalMessage(message);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
