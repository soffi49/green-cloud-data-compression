package agents.greenenergy.behaviour;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ImmutableServerRequestData;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static mapper.JsonMapper.getMapper;

public class HandleServerCallForProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleServerCallForProposal.class);
    private final MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.CFP);
    private GreenEnergyAgent myGreenAgent;

    private HandleServerCallForProposal(GreenEnergyAgent greenEnergyAgent) {
        super(greenEnergyAgent);
    }

    public static HandleServerCallForProposal createFor(GreenEnergyAgent greenEnergyAgent) {
        return new HandleServerCallForProposal(greenEnergyAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        myGreenAgent = (GreenEnergyAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(template);
        if (Objects.nonNull(message)) {
            try {
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                if (job.getPower() > ((GreenEnergyAgent) myAgent).getAvailableCapacity()) {
                    refuseCNA(message, "Refuse: Not enough available power");
                } else {
                    sendRequestToMonitoringAgent(message, job);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private void refuseCNA(ACLMessage message, String content) {
        ACLMessage response = new ACLMessage(ACLMessage.REFUSE);
        response.addReceiver(message.getSender());
        response.setContent(content);
        myAgent.send(response);
    }

    private void sendRequestToMonitoringAgent(ACLMessage message, Job job) {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(((GreenEnergyAgent) myAgent).getMonitoringAgent());
        request.setConversationId(message.getSender().getName());
        var requestData = ImmutableServerRequestData.builder()
                .location(myGreenAgent.getLocation())
                .job(job)
                .build();
        try {
            request.setContent(getMapper().writeValueAsString(requestData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.info("Sending request to monitoring agent");
        myAgent.send(request);
    }
}
