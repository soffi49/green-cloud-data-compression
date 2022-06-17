package messages.domain;

import domain.job.Job;
import jade.lang.acl.ACLMessage;

public class SendJobConfirmationMessage {
    private final ACLMessage message;
    private SendJobConfirmationMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobConfirmationMessage create(final String jobId, final ACLMessage replyMessage) {
        replyMessage.setPerformative(ACLMessage.INFORM);
        replyMessage.setContent(String.format("The execution of job %s started!", jobId));
        return new SendJobConfirmationMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
