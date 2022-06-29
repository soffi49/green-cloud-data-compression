package agents.greenenergy.behaviour;

import static common.GUIUtils.displayMessageArrow;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ImmutableGreenSourceRequestData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for requesting weather data from monitoring agent
 */
public class RequestWeatherData extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(RequestWeatherData.class);

    private final GreenEnergyAgent myGreenEnergyAgent;

    private final String conversationId;

    /**
     * Behaviour constructor.
     *
     * @param greenEnergyAgent agent which is executing the behaviour
     * @param conversationId   conversation identifier for given job processing
     */
    public RequestWeatherData(GreenEnergyAgent greenEnergyAgent, String conversationId) {
        myGreenEnergyAgent = greenEnergyAgent;
        this.conversationId = conversationId;
    }

    /**
     * Method which sends the request to the Monitoring Agent asking for the weather at the given location.
     */
    @Override
    public void action() {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(myGreenEnergyAgent.getMonitoringAgent());
        request.setConversationId(conversationId);
        var requestData = ImmutableGreenSourceRequestData.builder()
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
