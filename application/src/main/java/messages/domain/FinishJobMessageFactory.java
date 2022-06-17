package messages.domain;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class FinishJobMessageFactory {
    public static ACLMessage prepareFinishMessageForClient(final String jobId, final String clientId) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(FINISH_JOB_PROTOCOL);
        informationMessage.setContent(String.format("The job %s is finished!", jobId));
        informationMessage.addReceiver(new AID(clientId, AID.ISGUID));
        return informationMessage;
    }

    public static ACLMessage prepareFinishMessage(final String jobId, final List<AID> receivers) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(FINISH_JOB_PROTOCOL);
        informationMessage.setContent(jobId);
        receivers.forEach(informationMessage::addReceiver);
        return informationMessage;
    }
}
