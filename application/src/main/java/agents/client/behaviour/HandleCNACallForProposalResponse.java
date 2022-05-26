package agents.client.behaviour;

import agents.client.ClientAgent;
import agents.client.message.SendJobMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.CloudNetworkData;
import domain.job.Job;
import domain.job.PricedJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static jade.lang.acl.ACLMessage.*;
import static mapper.JsonMapper.getMapper;

/**
 * Cyclic behaviour for client agent. It purpose is to handle the call for proposal responses received from Cloud Network Agents
 */
public class HandleCNACallForProposalResponse extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleCNACallForProposalResponse.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                                                              MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
    private final Map<AID, PricedJob> cloudNetworkAgentsAccepting;
    private int responsesReceivedCount;
    private final Job job;

    private HandleCNACallForProposalResponse(final ClientAgent clientAgent, final Job job) {
        super(clientAgent);
        this.responsesReceivedCount = 0;
        this.cloudNetworkAgentsAccepting = new HashMap<>();
        this.job = job;
    }

    public static HandleCNACallForProposalResponse createFor(final ClientAgent clientAgent, final Job job) {
        return new HandleCNACallForProposalResponse(clientAgent, job);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            if (responsesReceivedCount < ((ClientAgent) myAgent).getMessagesSentCount()) {
                responsesReceivedCount++;
            }
            switch (message.getPerformative()) {
                case PROPOSE:
                    logger.info("[{}] {} sent the proposal", myAgent, message.getSender().getLocalName());
                    try {
                        cloudNetworkAgentsAccepting.put(message.getSender(), getMapper().readValue(message.getContent(), PricedJob.class));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (responsesReceivedCount == ((ClientAgent) myAgent).getMessagesSentCount()) {
                        final AID chosenCNA = chooseCNAToExecuteJob();
                        logger.info("[{}] Sending accepting proposal to {}", myAgent, chosenCNA.getLocalName());
                        ((ClientAgent) myAgent).setChosenCloudNetworkAgent(chosenCNA);
                        myAgent.send(SendJobMessage.create(job, List.of(chosenCNA), ACCEPT_PROPOSAL).getMessage());
                        rejectRemainingCNAAgents(chosenCNA);
                    }
                    break;
                case REFUSE:
                    logger.info("[{}] {} refused the call for proposal", myAgent, message.getSender().getLocalName());
                    if (responsesReceivedCount == ((ClientAgent) myAgent).getMessagesSentCount() && cloudNetworkAgentsAccepting.isEmpty()) {
                        logger.info("[{}] All Cloud Network Agents refused to the call for proposal", myAgent);
                        myAgent.doDelete();
                    }
                    break;
            }
        } else {
            block();
        }
    }

    private AID chooseCNAToExecuteJob() {
        final Comparator<Map.Entry<AID, PricedJob>> compareCNA =
                Comparator.comparingDouble(cna -> cna.getValue().getPriceForJob());
        return cloudNetworkAgentsAccepting.entrySet().stream().min(compareCNA).orElseThrow().getKey();
    }

    private void rejectRemainingCNAAgents(final AID chosenCNA) {
        final List<AID> cloudNetworkAgentsRejected =
                cloudNetworkAgentsAccepting.keySet().stream()
                        .filter(cloudNetworkData -> !cloudNetworkData.equals(chosenCNA))
                        .toList();
        myAgent.send(SendJobMessage.create(job, cloudNetworkAgentsRejected, REJECT_PROPOSAL).getMessage());
    }
}
