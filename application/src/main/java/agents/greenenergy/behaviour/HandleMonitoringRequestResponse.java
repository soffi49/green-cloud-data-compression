package agents.greenenergy.behaviour;

import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import domain.GreenSourceData;
import domain.ImmutableGreenSourceData;
import domain.MonitoringData;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleMonitoringRequestResponse extends CyclicBehaviour {

    private static final Logger logger =
        LoggerFactory.getLogger(HandleMonitoringRequestResponse.class);
    private final MessageTemplate template = MessageTemplate.or(
        MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
    private GreenEnergyAgent myGreenAgent;

    private HandleMonitoringRequestResponse(final GreenEnergyAgent greenEnergyAgent) {
        super(greenEnergyAgent);
    }

    public static HandleMonitoringRequestResponse createFor(final GreenEnergyAgent greenEnergyAgent) {
        return new HandleMonitoringRequestResponse(greenEnergyAgent);
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
            switch (message.getPerformative()) {
                case ACLMessage.REFUSE:
                    handleRefuse(message);
                    break;
                case ACLMessage.INFORM:
                    handleInform(message);
                    break;

            }
        }
    }

    private void handleRefuse(ACLMessage message) {
        ACLMessage response = new ACLMessage(ACLMessage.REFUSE);

        response.addReceiver(new AID(message.getConversationId(), AID.ISGUID));
        response.setContent("Refuse: weather data not available");
        myAgent.send(response);
    }

    private void handleInform(ACLMessage message) {
        try {
            MonitoringData data = getMapper().readValue(message.getContent(), MonitoringData.class);
            int power = computePower(data);
            if (power > 0) {
                var correspondingJob = myGreenAgent.getCurrentJobs().stream()
                    .filter(job -> job.getJobId().equals(data.getJobId()))
                    .findFirst()
                    .orElseThrow();
                GreenSourceData responseData = ImmutableGreenSourceData.builder()
                    .pricePerPowerUnit(myGreenAgent.getPricePerPowerUnit())
                    .availablePowerInTime(power)
                    .job(correspondingJob)
                    .build();
                ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
                var conversationID = message.getConversationId();
                response.addReceiver(new AID(conversationID, AID.ISGUID));
                response.setContent(getMapper().writeValueAsString(responseData));
                logger.info("Sending propose message to server");
                myAgent.send(response);
            } else {
                ACLMessage response = new ACLMessage(ACLMessage.REFUSE);
                response.addReceiver(new AID(message.getConversationId(), AID.ISGUID));
                response.setContent("Refuse: too bad weather conditions");
                logger.info("Sending refuse message to server");
                myAgent.send(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int computePower(MonitoringData data) {
        //TODO: implement power computation logic
        return 10;
    }
}
