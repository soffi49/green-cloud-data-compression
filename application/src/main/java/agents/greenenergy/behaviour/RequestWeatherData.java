package agents.greenenergy.behaviour;

import static agents.greenenergy.DataStoreConstants.JOB_MESSAGE;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ImmutableServerRequestData;
import domain.job.Job;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for requesting weather data from monitoring agent
 */
public class RequestWeatherData extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(RequestWeatherData.class);

    private GreenEnergyAgent myGreenEnergyAgent;

    private final String conversationId;

    public RequestWeatherData(GreenEnergyAgent greenEnergyAgent, String conversationId) {
        myGreenEnergyAgent = greenEnergyAgent;
        this.conversationId = conversationId;
    }

    @Override
    public void action() {
        ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
        request.addReceiver(myGreenEnergyAgent.getMonitoringAgent());
        request.setConversationId(conversationId);
        var requestData = ImmutableServerRequestData.builder()
                .location(myGreenEnergyAgent.getLocation())
                .job((Job) getParent().getDataStore().get(JOB_MESSAGE))
                .build();
        try {
            request.setContent(getMapper().writeValueAsString(requestData));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        myAgent.send(request);
    }
}
