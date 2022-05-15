package agents.cloudnetwork.behaviour;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.server.ServerAgent;
import agents.server.behaviour.HandleGreenSourceCallForProposalResponse;
import agents.server.message.ProposalResponseMessage;
import agents.server.message.RefuseProposalMessage;
import domain.GreenSourceData;
import domain.ServerData;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;

public class HandleServerCallForProposalResponse extends CyclicBehaviour {

    private static final ValueRange MAX_POWER_DIFFERENCE = ValueRange.of(-10, 10);
    private static final Logger logger = LoggerFactory.getLogger(HandleServerCallForProposalResponse.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(PROPOSE),
                                                                              MessageTemplate.MatchPerformative(REFUSE));

    private Map<AID, ServerData> serverAgentsAccepting;
    private int responsesReceivedCount;

    private HandleServerCallForProposalResponse(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
        this.responsesReceivedCount = 0;
        this.serverAgentsAccepting = new HashMap<>();
    }

    public static HandleServerCallForProposalResponse createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleServerCallForProposalResponse(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            if (responsesReceivedCount < ((ServerAgent) myAgent).getMessagesSentCount()) {
                responsesReceivedCount++;
            }

            switch (message.getPerformative()) {
                case PROPOSE:
                    try {
                        logger.info("[{}] {} sent the proposal", myAgent, message.getSender().getLocalName());
                        serverAgentsAccepting.put(message.getSender(), (ServerData) message.getContentObject());
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }

                    if (responsesReceivedCount == ((CloudNetworkAgent) myAgent).getMessagesSentCount()) {
                        final AID chosenServer = chooseServerToExecuteJob();
                        ((CloudNetworkAgent) myAgent).setChosenServer(chosenServer);
                        //TODO send message back to client
                    }
                    break;
                case REFUSE:
                    if (serverAgentsAccepting.isEmpty()) {
                        logger.info("[{}] No servers available - sending refuse message ", myAgent);
                        //TODO send message back to client
                    }
            }
        } else {
            block();
        }
    }

    private AID chooseServerToExecuteJob() {
        final Comparator<Map.Entry<AID, ServerData>> compareServers =
                ((server1, server2) -> {
                   if(MAX_POWER_DIFFERENCE.isValidIntValue(server1.getValue().getPowerInUse() - server2.getValue().getPowerInUse())) {
                       return (int) (server1.getValue().getPricePerHour() - server2.getValue().getPricePerHour());
                   }
                   return server1.getValue().getPowerInUse() - server2.getValue().getPowerInUse();
                });
        return serverAgentsAccepting.entrySet().stream().min(compareServers).orElseThrow().getKey();
    }
}
