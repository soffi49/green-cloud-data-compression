package agents.server.message;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

import agents.server.ServerAgent;
import jade.lang.acl.ACLMessage;

public class RefuseProposalMessage {

    private final ACLMessage message;

    private RefuseProposalMessage(ACLMessage message) {
        this.message = message;
    }

    public static RefuseProposalMessage create(ServerAgent serverAgent) {
        ACLMessage message = new ACLMessage(REJECT_PROPOSAL);
        message.setContent("Refuse");
        message.addReceiver(serverAgent.getOwnerCloudNetworkAgent());
        return new RefuseProposalMessage(message);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
