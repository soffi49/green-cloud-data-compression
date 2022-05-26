package agents.server.behaviour;

import static jade.lang.acl.ACLMessage.*;
import static java.time.temporal.ChronoUnit.HOURS;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import agents.server.message.ProposalResponseMessage;
import agents.server.message.RefuseProposalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.GreenSourceData;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleGreenSourceCallForProposalResponse extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleGreenSourceCallForProposalResponse.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.or(
        MessageTemplate.MatchPerformative(PROPOSE),
        MessageTemplate.MatchPerformative(REFUSE));

    private Map<AID, GreenSourceData> greenSourceAgentsAccepting;
    private int responsesReceivedCount;
    private ServerAgent serverAgent;

    private HandleGreenSourceCallForProposalResponse(final ServerAgent serverAgent) {
        super(serverAgent);
        this.responsesReceivedCount = 0;
        this.greenSourceAgentsAccepting = new HashMap<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        serverAgent = (ServerAgent) myAgent;
    }

    public static HandleGreenSourceCallForProposalResponse createFor(final ServerAgent serverAgent) {
        return new HandleGreenSourceCallForProposalResponse(serverAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            if (responsesReceivedCount < ((ServerAgent) myAgent).getMessagesSentCount()) {
                responsesReceivedCount++;
            }

            GreenSourceData data = null;

            try {
                data = getMapper().readValue(message.getContent(), GreenSourceData.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if (data == null) {
                throw new RuntimeException("what are you doing?!");
            }

            switch (message.getPerformative()) {
                case PROPOSE:
                    logger.info("received proposal from {} [{}]", myAgent, message.getSender().getLocalName());
                    greenSourceAgentsAccepting.put(message.getSender(), data);
                    if (responsesReceivedCount == serverAgent.getMessagesSentCount()) {
                        final var chosenGS = chooseGreenSourceToExecuteJob();
                        serverAgent.getGreenSourceForJobMap().put(chosenGS.getValue().getJob(), chosenGS.getKey());
                        rejectRemainingGreenSourceAgents(chosenGS.getKey());
                        logger.info("[{}] Sending proposal to {}", myAgent,
                            serverAgent.getOwnerCloudNetworkAgent().getLocalName());
                        final double servicePrice = calculateServicePrice(chosenGS.getValue());
                        myAgent.send(ProposalResponseMessage.create(serverAgent,
                            servicePrice, data.getJob()).getMessage());
                    }
                    break;
                case REFUSE:
                    if (greenSourceAgentsAccepting.isEmpty()) {
                        logger.info("[{}] No green sources available - sending refuse message ", myAgent);
                        myAgent.send(RefuseProposalMessage.create(serverAgent, data.getJob().getClientIdentifier())
                            .getMessage());
                    }
            }
        } else {
            block();
        }
    }

    private double calculateServicePrice(final GreenSourceData greenSourceData) {
        var powerCost = greenSourceData.getJob().getPower() * greenSourceData.getPricePerPowerUnit();
        var computingCost =
            HOURS.between(greenSourceData.getJob().getEndTime(), greenSourceData.getJob().getStartTime())
                * serverAgent.getPricePerHour();
        return powerCost + computingCost;
    }

    private Map.Entry<AID, GreenSourceData> chooseGreenSourceToExecuteJob() {
        final Comparator<Map.Entry<AID, GreenSourceData>> compareGreenSources =
            Comparator.comparingInt(cna -> cna.getValue().getAvailablePowerInTime());
        return greenSourceAgentsAccepting.entrySet().stream().min(compareGreenSources).orElseThrow();
    }

    private void rejectRemainingGreenSourceAgents(final AID chosenGreenSource) {
        final List<AID> greenSourceAgentsRejected = greenSourceAgentsAccepting.keySet().stream()
                .filter(cloudNetworkData -> !cloudNetworkData.equals(chosenGreenSource))
                .toList();
        final ACLMessage rejectProposal = new ACLMessage(REJECT_PROPOSAL);
        rejectProposal.setContent("Reject");
        greenSourceAgentsRejected.forEach(rejectProposal::addReceiver);
        myAgent.send(rejectProposal);
    }
}
