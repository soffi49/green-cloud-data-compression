package messages;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

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
    public static void rejectJobOffers(final Agent agent, final Job job, final ACLMessage chosenOffer, final List<ACLMessage> receivedOffers) {
        receivedOffers.stream()
                .filter(offer -> !offer.equals(chosenOffer))
                .forEach(offer -> agent.send(SendJobOfferResponseMessage.create(job, REJECT_PROPOSAL, offer.createReply()).getMessage()));
    }
}
