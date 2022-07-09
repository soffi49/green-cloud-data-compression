package agents.monitoring.behaviour;

import static agents.monitoring.MonitoringAgentConstants.OFFLINE_MODE;
import static common.GUIUtils.displayMessageArrow;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static mapper.JsonMapper.getMapper;

import agents.monitoring.MonitoringAgent;
import domain.GreenSourceForecastData;
import domain.ImmutableMonitoringData;
import domain.ImmutableWeatherData;
import domain.MonitoringData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for listening for the upcoming forecast requests
 */
public class ServeForecastInformation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ServeForecastInformation.class);
    private static final MessageTemplate template = MessageTemplate.MatchPerformative(REQUEST);

    private final MonitoringAgent monitoringAgent;

    /**
     * Behaviour constructor.
     *
     * @param monitoringAgent agent which is executing the behaviour
     */
    public ServeForecastInformation(MonitoringAgent monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    /**
     * Method which listens for the request for weather data coming from the Green Source Agents. It retrieves the
     * forecast information for the given location and forwards it as a reply to the sender.
     */
    @Override
    public void action() {
        final ACLMessage message = monitoringAgent.receive(template);

        if (Objects.nonNull(message)) {
            final ACLMessage response = message.createReply();
            response.setPerformative(INFORM);
            try {
                var requestData = getMapper()
                    .readValue(message.getContent(), GreenSourceForecastData.class);
                if(OFFLINE_MODE) {
                    response.setContent(getMapper().writeValueAsString(
                        useStubData())
                    );
                } else {
                    response.setContent(getMapper().writeValueAsString(
                        useApi(requestData))
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.setConversationId(message.getConversationId());
            logger.info("Sending message with the weather data");
            displayMessageArrow(monitoringAgent, message.getSender());
            monitoringAgent.send(response);
        } else {
            block();
        }
    }

    private MonitoringData useApi(GreenSourceForecastData requestData) {
        return monitoringAgent.getForecast(requestData);
    }

    private MonitoringData useStubData() {
        return ImmutableMonitoringData.builder()
            .addWeatherData(ImmutableWeatherData.builder()
                .cloudCover(25.0)
                .temperature(25.0)
                .windSpeed(10.0)
                .time(Instant.now())
                .build())
            .build();
    }
}
