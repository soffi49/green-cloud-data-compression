package agents.greenenergy.behaviour;

import agents.greenenergy.GreenEnergyAgent;
import domain.ImmutableMonitoringData;
import domain.MonitoringData;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static mapper.JsonMapper.getMapper;

public class HandleMonitoringRequestResponse extends CyclicBehaviour {
    private static final Logger logger =
            LoggerFactory.getLogger(HandleMonitoringRequestResponse.class);
    private final MessageTemplate template = MessageTemplate.or(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchPerformative(ACLMessage.REFUSE));

    private HandleMonitoringRequestResponse(final GreenEnergyAgent greenEnergyAgent) {
        super(greenEnergyAgent);
    }

    public static HandleMonitoringRequestResponse createFor(final GreenEnergyAgent greenEnergyAgent) {
        return new HandleMonitoringRequestResponse(greenEnergyAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(template);

        if(Objects.nonNull(message)){
            switch (message.getPerformative()){
                case ACLMessage.REFUSE:
                    handleRefuse(message);
                    break;
                case ACLMessage.INFORM:
                    handleInform(message);
                    break;

            }
        }
    }

    private void handleRefuse(ACLMessage message){
        ACLMessage response = new ACLMessage(ACLMessage.REFUSE);

        response.addReceiver(new AID(message.getConversationId(), AID.ISGUID));
        response.setContent("Refuse: weather data not available");
        myAgent.send(response);
    }

    private void handleInform(ACLMessage message){
        try{
            MonitoringData data = getMapper().readValue(message.getContent(), MonitoringData.class);
            if(computePower(data)){

            }
            else{
                ACLMessage response = new ACLMessage(ACLMessage.REFUSE);
                response.addReceiver(new AID(message.getConversationId(), AID.ISGUID));
                response.setContent("Refuse: too bad weather conditions");
                myAgent.send(response);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private boolean computePower(MonitoringData data){
        //TODO: implement power computation logic
        return true;
    }
}
