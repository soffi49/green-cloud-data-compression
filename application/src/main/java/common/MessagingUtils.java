package common;

import common.message.SendJobOfferResponseMessage;
import domain.job.Job;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.List;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;

public class MessagingUtils {
    public static void rejectJobOffers(final Agent agent, final Job job, final ACLMessage chosenOffer, final List<ACLMessage> receivedOffers) {
        receivedOffers.stream()
                .filter(offer -> !offer.equals(chosenOffer))
                .forEach(offer -> agent.send(SendJobOfferResponseMessage.create(job, REJECT_PROPOSAL, offer.createReply()).getMessage()));
    }
}
