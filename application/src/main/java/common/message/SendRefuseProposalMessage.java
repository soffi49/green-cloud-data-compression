package common.message;

import jade.lang.acl.ACLMessage;

import static jade.lang.acl.ACLMessage.REFUSE;

public class SendRefuseProposalMessage {

    private final ACLMessage message;

    private SendRefuseProposalMessage(ACLMessage message) {
        this.message = message;
    }

    public static SendRefuseProposalMessage create(final ACLMessage replyMessage) {
        replyMessage.setPerformative(REFUSE);
        replyMessage.setContent("REFUSE");
        return new SendRefuseProposalMessage(replyMessage);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
