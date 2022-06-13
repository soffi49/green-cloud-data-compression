package messages.domain;

import domain.job.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;

public class SendJobFinishedMessage {

    private final ACLMessage message;
    private SendJobFinishedMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendJobFinishedMessage create(final Job job) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(FINISH_JOB_PROTOCOL);
        informationMessage.setContent(String.format("The job %s is finished!", job.getJobId()));
        informationMessage.addReceiver(new AID(job.getClientIdentifier(), AID.ISGUID));
        return new SendJobFinishedMessage(informationMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
