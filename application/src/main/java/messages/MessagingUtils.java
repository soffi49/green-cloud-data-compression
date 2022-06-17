package messages;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

import messages.domain.ReplyMessageFactory;
import messages.domain.SendJobOfferResponseMessage;
import domain.job.Job;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.List;
import java.util.Vector;

public class MessagingUtils {
    public static List<ACLMessage> retrieveProposals(final Vector responses) {
        return ((Vector<ACLMessage>) responses).stream()
                .filter(response -> response.getPerformative() == ACLMessage.PROPOSE)
                .toList();
    }
    public static void rejectJobOffers(final Agent agent, final String jobId, final ACLMessage chosenOffer, final List<ACLMessage> receivedOffers) {
        receivedOffers.stream()
                .filter(offer -> !offer.equals(chosenOffer))
                .forEach(offer -> agent.send(ReplyMessageFactory.prepareReply(offer.createReply(), jobId ,REJECT_PROPOSAL)));
    }
}
