package message.client;


import domain.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.List;

public class SendJobMessage {

    private final ACLMessage message;

    private SendJobMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobMessage create(Job job, List<AID> receiverList, int performative) {
        final ACLMessage proposal = new ACLMessage(performative);
        try {
            proposal.setContentObject(job);
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiverList.forEach(proposal::addReceiver);
        return new SendJobMessage(proposal);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
