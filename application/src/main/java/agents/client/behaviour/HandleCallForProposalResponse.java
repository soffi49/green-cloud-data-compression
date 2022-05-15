package agents.client.behaviour;

import agents.client.ClientAgent;
import agents.client.message.SendJobMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.CloudNetworkData;
import domain.job.Job;
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
public class HandleCallForProposalResponse extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleCallForProposalResponse.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                                                              MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
    private final Map<AID, CloudNetworkData> cloudNetworkAgentsAccepting;
    private int responsesReceivedCount;
    private final Job job;

    private HandleCallForProposalResponse(final ClientAgent clientAgent, final Job job) {
        super(clientAgent);
        this.responsesReceivedCount = 0;
        this.cloudNetworkAgentsAccepting = new HashMap<>();
        this.job = job;
    }

    public static HandleCallForProposalResponse createFor(final ClientAgent clientAgent, final Job job) {
        return new HandleCallForProposalResponse(clientAgent, job);
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
                        cloudNetworkAgentsAccepting.put(message.getSender(), getMapper().readValue(message.getContent(), CloudNetworkData.class));
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
        final Comparator<Map.Entry<AID, CloudNetworkData>> compareCNA =
                Comparator.comparingInt(cna -> cna.getValue().getJobsCount());
        return cloudNetworkAgentsAccepting.entrySet().stream().min(compareCNA).orElseThrow().getKey();
    }

    private void rejectRemainingCNAAgents(final AID chosenCNA) {
        final List<AID> cloudNetworkAgentsRejected =
                cloudNetworkAgentsAccepting.keySet().stream()
                        .filter(cloudNetworkData -> !cloudNetworkData.equals(chosenCNA))
                        .collect(Collectors.toList());
        final ACLMessage rejectProposal = new ACLMessage(REJECT_PROPOSAL);
        rejectProposal.setContent("Proposal rejected");
        cloudNetworkAgentsRejected.forEach(rejectProposal::addReceiver);
        myAgent.send(rejectProposal);
    }
}
