package agents.monitoring.behaviour;

import static jade.lang.acl.ACLMessage.REQUEST;
import static mapper.JsonMapper.getMapper;

import agents.monitoring.MonitoringAgent;
import domain.ServerRequestData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServeWeatherInformation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    private final MonitoringAgent monitoringAgent;

    public ServeWeatherInformation(MonitoringAgent monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    public static ServeWeatherInformation createFor(MonitoringAgent monitoringAgent) {
        return new ServeWeatherInformation(monitoringAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = monitoringAgent.receive();

        if (Objects.nonNull(message)) {
            switch (message.getPerformative()) {
                case REQUEST:
                    final ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                    try {
                        var requestData = getMapper().readValue(message.getContent(), ServerRequestData.class);
                        var data = monitoringAgent.getWeather(requestData);
                        response.setContent(getMapper().writeValueAsString(data));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    response.addReceiver(message.getSender());
                    response.setConversationId(message.getConversationId());
                    logger.info("Sending message with the weather data");
                    monitoringAgent.send(response);
            }
        } else {
            block();
        }
    }
}
