package messages.domain;

import domain.job.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

import static mapper.JsonMapper.getMapper;

public class SendJobCallForProposalMessage {
    private final ACLMessage message;
    private SendJobCallForProposalMessage(final ACLMessage message) {
        this.message = message;
    }

    public static SendJobCallForProposalMessage create(final Job job, final List<AID> receiverList, final String protocol) {
        final ACLMessage proposal = new ACLMessage(ACLMessage.CFP);
        proposal.setProtocol(protocol);
        try {
            proposal.setContent(getMapper().writeValueAsString(job));
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiverList.forEach(proposal::addReceiver);
        return new SendJobCallForProposalMessage(proposal);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
