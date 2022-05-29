package agents.cloudnetwork.message;

import domain.job.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class SendJobConfirmationMessage {
    private final ACLMessage message;
    private SendJobConfirmationMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobConfirmationMessage create(final Job job, final ACLMessage replyMessage) {
        replyMessage.setPerformative(ACLMessage.INFORM);
        replyMessage.setContent(String.format("The execution of job %s started!", job.getJobId()));
        return new SendJobConfirmationMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
