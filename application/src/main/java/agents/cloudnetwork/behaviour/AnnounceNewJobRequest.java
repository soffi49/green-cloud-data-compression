package agents.cloudnetwork.behaviour;

import static common.MessagingUtils.rejectJobOffers;
import static mapper.JsonMapper.getMapper;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.message.SendJobOfferMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.message.SendRefuseProposalMessage;
import domain.ServerData;
import exception.IncorrectServerOfferException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Vector;

/**
 * Behaviour which is responsible for broadcasting client's job to servers and choosing server to execute the job
 */
public class AnnounceNewJobRequest extends ContractNetInitiator {
    private static final Logger logger = LoggerFactory.getLogger(AnnounceNewJobRequest.class);
    private static final ValueRange MAX_POWER_DIFFERENCE = ValueRange.of(-10, 10);
    private final ACLMessage replyMessage;
    private CloudNetworkAgent myCloudNetworkAgent;

    public AnnounceNewJobRequest(final Agent a, final ACLMessage cfp, final ACLMessage replyMessage) {
        super(a, cfp);
        this.replyMessage = replyMessage;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    protected void handleAllResponses(final Vector responses, final Vector acceptances) {
        final List<ACLMessage> proposals = ((Vector<ACLMessage>) responses).stream()
                .filter(response -> response.getPerformative() == ACLMessage.PROPOSE)
                .toList();

        if (responses.isEmpty()) {
            logger.info("[{}] No responses were retrieved", myAgent.getName());
        } else if (proposals.isEmpty()) {
            logger.info("[{}] No servers available - sending refuse message to client", myAgent.getName());
            myAgent.send(SendRefuseProposalMessage.create(replyMessage).getMessage());
        } else {
            final ACLMessage chosenServerOffer = chooseServerToExecuteJob(proposals);
            getDataStore().put(chosenServerOffer.getSender(), chosenServerOffer.createReply());
            logger.info("[{}] Chosen Server for the job: {}", myAgent.getName(), chosenServerOffer.getSender().getLocalName());

            ServerData chosenServerData;
            try {
                chosenServerData = getMapper().readValue(chosenServerOffer.getContent(), ServerData.class);
            } catch (JsonProcessingException e) {
                throw new IncorrectServerOfferException();
            }

            logger.info("[{}] Sending job execution offer to Client", myAgent.getName());
            myCloudNetworkAgent.getServerForJobMap().put(chosenServerData.getJob(), chosenServerOffer.getSender());
            myAgent.addBehaviour(new ProposeJobOffer(myAgent, SendJobOfferMessage.create(chosenServerData, replyMessage).getMessage(), getDataStore()));
            rejectJobOffers(myCloudNetworkAgent, chosenServerData.getJob(), chosenServerOffer, proposals);
        }
    }

    private ACLMessage chooseServerToExecuteJob(final List<ACLMessage> serverOffers) {
        return serverOffers.stream().min(this::compareServerOffers).orElseThrow();
    }

    private int compareServerOffers(final ACLMessage serverOffer1, final ACLMessage serverOffer2) {
        ServerData server1;
        ServerData server2;
        try {
            server1 = getMapper().readValue(serverOffer1.getContent(), ServerData.class);
            server2 = getMapper().readValue(serverOffer2.getContent(), ServerData.class);
        } catch (JsonProcessingException e) {
            throw new IncorrectServerOfferException();
        }
        int powerDifference = server1.getPowerInUse() - server2.getPowerInUse();
        int priceDifference = (int) (server1.getPricePerHour() - server2.getPricePerHour());
        return MAX_POWER_DIFFERENCE.isValidIntValue(powerDifference) ? priceDifference : powerDifference;
    }
}
