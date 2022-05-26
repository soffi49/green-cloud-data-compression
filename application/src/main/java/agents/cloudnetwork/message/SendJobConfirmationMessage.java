package agents.cloudnetwork.message;

import domain.job.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SendJobConfirmationMessage {

    private final ACLMessage message;

    private SendJobConfirmationMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobConfirmationMessage create(final Job job) {
        final ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setContent(String.format("The execution of job %s started!", job.getJobId()));
        response.setConversationId("STARTED");
        response.addReceiver(new AID(job.getClientIdentifier(), AID.ISGUID));
        return new SendJobConfirmationMessage(response);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
