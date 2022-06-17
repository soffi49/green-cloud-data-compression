package messages.domain;

import static mapper.JsonMapper.getMapper;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

public class CallForProposalMessageFactory {
    private final ACLMessage message;

    private CallForProposalMessageFactory(final ACLMessage message) {
        this.message = message;
    }

    public static CallForProposalMessageFactory create(final Object content, final List<AID> receiverList, final String protocol) {
        final ACLMessage proposal = new ACLMessage(ACLMessage.CFP);
        proposal.setProtocol(protocol);
        try {
            proposal.setContent(getMapper().writeValueAsString(content));
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiverList.forEach(proposal::addReceiver);
        return new CallForProposalMessageFactory(proposal);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
