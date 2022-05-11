package agents.server.message;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

import agents.server.ServerAgent;
import jade.lang.acl.ACLMessage;

public class ProposalResponseMessage {

    private final ACLMessage message;

    private ProposalResponseMessage(ACLMessage message) {
        this.message = message;
    }

    public static ProposalResponseMessage create(ServerAgent serverAgent, int messageType) {
        ACLMessage message = new ACLMessage(messageType);
        if (messageType == REJECT_PROPOSAL) {
            message.setContent("Reject");
        } else {
            message.setContent("Accept");
        }
        message.addReceiver(serverAgent.getOwnerCloudNetworkAgent());
        return new ProposalResponseMessage(message);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
