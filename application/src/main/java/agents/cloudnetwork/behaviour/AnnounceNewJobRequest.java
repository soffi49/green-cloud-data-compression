package agents.cloudnetwork.behaviour;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.message.SendJobOfferMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ServerData;
import exception.IncorrectServerOfferException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import static common.MessagingUtils.rejectJobOffers;
import static jade.lang.acl.ACLMessage.REFUSE;
import static mapper.JsonMapper.getMapper;

/**
 * Behaviour which is responsible for broadcasting client's job to servers and choosing server to execute the job
 */
public class AnnounceNewJobRequest extends ContractNetInitiator {
    private static final Logger logger = LoggerFactory.getLogger(AnnounceNewJobRequest.class);
    private static final ValueRange MAX_POWER_DIFFERENCE = ValueRange.of(-10, 10);

    private CloudNetworkAgent myCloudNetworkAgent;
    private final AID client;

    public AnnounceNewJobRequest(final Agent a, final ACLMessage clientsJob, final DataStore dataStore, final AID client) {
        super(a, clientsJob, dataStore);
        this.client = client;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    protected void handleAllResponses(final Vector responses, final Vector acceptances) {
        if (responses.isEmpty()) {
            logger.info("[{}] No responses were retrieved", myAgent);
        } else if (acceptances.isEmpty()) {
            logger.info("[{}] No servers available - sending refuse message to client", myAgent);
            final ACLMessage replyMessage = (ACLMessage) getDataStore().get(client);
            replyMessage.setPerformative(REFUSE);
            replyMessage.setContent("REFUSE");
            myAgent.send(replyMessage);
        } else {
            logger.info("[{}] Sending job execution offer to client", myAgent);
            final ACLMessage chosenServerOffer = chooseServerToExecuteJob((Vector<ACLMessage>) acceptances);
            getDataStore().put(chosenServerOffer.getSender(), chosenServerOffer);
            final ACLMessage replyMessage = (ACLMessage) getDataStore().get(client);

            ServerData chosenServerData;
            try {
                chosenServerData = getMapper().readValue(chosenServerOffer.getContent(), ServerData.class);
            } catch (JsonProcessingException e) {
                throw new IncorrectServerOfferException();
            }

            myCloudNetworkAgent.getServerForJobMap().put(chosenServerData.getJob(), chosenServerOffer.getSender());
            myAgent.addBehaviour(new ProposeJobOffer(myAgent, SendJobOfferMessage.create(chosenServerData, replyMessage).getMessage(), getDataStore()));
            rejectJobOffers(myAgent, chosenServerData.getJob(), chosenServerOffer, acceptances);
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
