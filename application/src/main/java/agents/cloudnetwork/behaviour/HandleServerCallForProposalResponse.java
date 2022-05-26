package agents.cloudnetwork.behaviour;

import agents.cloudnetwork.CloudNetworkAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ServerData;
import domain.job.ImmutablePricedJob;
import domain.job.PricedJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ValueRange;
import java.util.*;

import static jade.core.AID.ISGUID;
import static jade.lang.acl.ACLMessage.*;
import static mapper.JsonMapper.getMapper;

public class HandleServerCallForProposalResponse extends CyclicBehaviour {

    private static final ValueRange MAX_POWER_DIFFERENCE = ValueRange.of(-10, 10);
    private static final Logger logger = LoggerFactory.getLogger(HandleServerCallForProposalResponse.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.or(
            MessageTemplate.MatchPerformative(PROPOSE),
            MessageTemplate.MatchPerformative(REFUSE));

    private Map<AID, ServerData> serverAgentsAccepting;
    private int responsesReceivedCount;
    private CloudNetworkAgent myCloudAgent;

    private HandleServerCallForProposalResponse(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
        this.responsesReceivedCount = 0;
        this.serverAgentsAccepting = new HashMap<>();
    }

    public static HandleServerCallForProposalResponse createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleServerCallForProposalResponse(cloudNetworkAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        myCloudAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            if (responsesReceivedCount < myCloudAgent.getMessagesSentCount()) {
                responsesReceivedCount++;
            }

            switch (message.getPerformative()) {
                case PROPOSE:
                    logger.info("[{}] {} sent the proposal", myAgent, message.getSender().getLocalName());
                    try {
                        var data = getMapper().readValue(message.getContent(), ServerData.class);
                        serverAgentsAccepting.put(message.getSender(), data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (responsesReceivedCount == myCloudAgent.getMessagesSentCount()) {
                        final var chosenServer = chooseServerToExecuteJob();
                        myCloudAgent.getServerForJobMap().put(chosenServer.getValue().getJob(), chosenServer.getKey());
                        rejectRemainingServerAgents(chosenServer.getKey());
                        final PricedJob pricedJob = ImmutablePricedJob.builder()
                                .job(chosenServer.getValue().getJob())
                                .priceForJob(chosenServer.getValue().getServicePrice())
                                .build();
                        final ACLMessage propose = new ACLMessage(PROPOSE);
                        propose.addReceiver(new AID(chosenServer.getValue().getJob().getClientIdentifier(), ISGUID));
                        try {
                            propose.setContent(getMapper().writeValueAsString(pricedJob));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        myAgent.send(propose);
                    }
                    break;
                case REFUSE:
                    if (serverAgentsAccepting.isEmpty()) {
                        logger.info("[{}] No servers available - sending refuse message to client", myAgent);
                        var client = message.getConversationId();
                        var refuseMessage = new ACLMessage(REFUSE);
                        refuseMessage.setContent("Refuse");
                        refuseMessage.addReceiver(new AID(client, ISGUID));
                        myAgent.send(refuseMessage);
                    }
            }
        } else {
            block();
        }
    }

    private Map.Entry<AID, ServerData> chooseServerToExecuteJob() {
        final Comparator<Map.Entry<AID, ServerData>> compareServers =
                ((server1, server2) -> {
                    if (MAX_POWER_DIFFERENCE.isValidIntValue(
                            server1.getValue().getPowerInUse() - server2.getValue().getPowerInUse())) {
                        return (int) (server1.getValue().getPricePerHour() - server2.getValue().getPricePerHour());
                    }
                    return server1.getValue().getPowerInUse() - server2.getValue().getPowerInUse();
                });
        return serverAgentsAccepting.entrySet().stream().min(compareServers).orElseThrow();
    }

    private void rejectRemainingServerAgents(final AID chosenServer) {
        final List<AID> serverAgentsRejected = serverAgentsAccepting.keySet().stream()
                .filter(cloudNetworkData -> !cloudNetworkData.equals(chosenServer))
                .toList();
        final ACLMessage rejectProposal = new ACLMessage(REJECT_PROPOSAL);
        rejectProposal.setContent("Reject");
        serverAgentsRejected.forEach(rejectProposal::addReceiver);
        myAgent.send(rejectProposal);
    }
}
