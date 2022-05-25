package agents.server.behaviour;

import agents.server.ServerAgent;
import agents.server.message.ProposalResponseMessage;
import agents.server.message.RefuseProposalMessage;
import domain.GreenSourceData;
import domain.ImmutableGreenSourceData;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REFUSE;
import static mapper.JsonMapper.getMapper;

public class HandleGreenSourceCallForProposalResponse extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleGreenSourceCallForProposalResponse.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(PROPOSE),
                                                                              MessageTemplate.MatchPerformative(REFUSE));

    private Map<AID, GreenSourceData> greenSourceAgentsAccepting;
    private int responsesReceivedCount;

    private HandleGreenSourceCallForProposalResponse(final ServerAgent serverAgent) {
        super(serverAgent);
        this.responsesReceivedCount = 0;
        this.greenSourceAgentsAccepting = new HashMap<>();
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

            switch (message.getPerformative()) {
                case PROPOSE:
                    logger.info("[{}] {} sent the proposal", myAgent, message.getSender().getLocalName());
                    try{
                        final GreenSourceData data = getMapper().readValue(message.getContent(), GreenSourceData.class);
                        greenSourceAgentsAccepting.put(message.getSender(), data);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                    if (responsesReceivedCount == ((ServerAgent) myAgent).getMessagesSentCount()) {
                        final AID chosenGS = chooseGreenSourceToExecuteJob();
                        ((ServerAgent) myAgent).setChosenGreenSource(chosenGS);
                        logger.info("[{}] Sending proposal to {}", myAgent, ((ServerAgent) myAgent).getOwnerCloudNetworkAgent().getLocalName());
                        myAgent.send(ProposalResponseMessage.create((ServerAgent) myAgent).getMessage());
                    }
                    break;
                case REFUSE:
                    if (greenSourceAgentsAccepting.isEmpty()) {
                        logger.info("[{}] No green sources available - sending refuse message ", myAgent);
                        myAgent.send(RefuseProposalMessage.create((ServerAgent) myAgent).getMessage());
                    }
            }
        } else {
            block();
        }
    }

    private AID chooseGreenSourceToExecuteJob() {
        final Comparator<Map.Entry<AID, GreenSourceData>> compareGreenSources =
                Comparator.comparingInt(cna -> cna.getValue().getAvailablePowerInTime());
        return greenSourceAgentsAccepting.entrySet().stream().min(compareGreenSources).orElseThrow().getKey();
    }
}
