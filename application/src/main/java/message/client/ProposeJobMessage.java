package message.client;

import static jade.lang.acl.ACLMessage.REQUEST;

import domain.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.IOException;

public class ProposeJobMessage {

    private final ACLMessage message;

    private ProposeJobMessage(ACLMessage message) {
        this.message = message;
    }

    public static ProposeJobMessage create(Job job, AID receiver) {
        final ACLMessage proposal = new ACLMessage(REQUEST);
        try {
            proposal.setContentObject(job);
        } catch (IOException e) {
            e.printStackTrace();
        }
        proposal.addReceiver(receiver);
        return new ProposeJobMessage(proposal);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
