package agents.greenenergy.behaviour.powercheck;

import static common.GUIUtils.displayMessageArrow;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ImmutableGreenSourceWeatherData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Objects;

/**
 * Behaviour responsible for requesting weather data from monitoring agent
 */
public class RequestWeatherData extends OneShotBehaviour {

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final ACLMessage message;
    private final String protocol;
    private final String conversationId;

    /**
     * Behaviour constructor.
     *
     * @param greenEnergyAgent agent which is executing the behaviour
     * @param message          request message that was sent to green energy agent
     */
    public RequestWeatherData(GreenEnergyAgent greenEnergyAgent, ACLMessage message) {
        myGreenEnergyAgent = greenEnergyAgent;
        this.message = message;
        this.protocol = null;
        this.conversationId = null;
    }

    /**
     * Behaviour constructor.
     *
     * @param greenEnergyAgent agent which is executing the behaviour
     * @param protocol         protocol of the message
     * @param conversationId   conversation id of the message
     */
    public RequestWeatherData(GreenEnergyAgent greenEnergyAgent, String protocol, String conversationId) {
        myGreenEnergyAgent = greenEnergyAgent;
        this.message = null;
        this.protocol = protocol;
        this.conversationId = conversationId;

    }

    /**
     * Method which sends the request to the Monitoring Agent asking for the weather at the given location.
     */
    @Override
    public void action() {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(myGreenEnergyAgent.getMonitoringAgent());
        if(Objects.nonNull(message)) {
            request.setConversationId(message.getConversationId());
            request.setProtocol(message.getProtocol());
        } else {
            request.setConversationId(conversationId);
            request.setProtocol(protocol);
        }
        var requestData = ImmutableGreenSourceWeatherData.builder()
                .location(myGreenEnergyAgent.getLocation())
                .build();
        try {
            request.setContent(getMapper().writeValueAsString(requestData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getMonitoringAgent());
        myAgent.send(request);
    }
}
