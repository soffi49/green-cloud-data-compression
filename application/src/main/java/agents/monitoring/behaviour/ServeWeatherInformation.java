package agents.monitoring.behaviour;

import static agents.monitoring.MonitoringAgentConstants.OFFLINE_MODE;
import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.SERVER_JOB_START_CHECK_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;

import agents.monitoring.MonitoringAgent;
import domain.GreenSourceWeatherData;
import domain.ImmutableMonitoringData;
import domain.ImmutableWeatherData;
import domain.MonitoringData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for listening for the upcoming forecast requests
 */
public class ServeWeatherInformation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ServeWeatherInformation.class);
    private static final MessageTemplate template = and(MatchPerformative(REQUEST),
        MatchProtocol(SERVER_JOB_START_CHECK_PROTOCOL));

    private final MonitoringAgent monitoringAgent;

    /**
     * Behaviour constructor.
     *
     * @param monitoringAgent agent which is executing the behaviour
     */
    public ServeWeatherInformation(MonitoringAgent monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    /**
     * Method which listens for the request for weather data coming from the Green Source Agents. It retrieves the
     * weather information for the given location and forwards it as a reply to the sender.
     */
    @Override
    public void action() {
        final ACLMessage message = monitoringAgent.receive(template);

        if (Objects.nonNull(message)) {
            final ACLMessage response = message.createReply();
            response.setPerformative(INFORM);
            try {
                var requestData = getMapper()
                    .readValue(message.getContent(), GreenSourceWeatherData.class);
                if(OFFLINE_MODE) {
                    // TODO remove Random - use GUI button
                    if( new Random().nextInt(100) <= 3) {
                        logger.warn("[{}] Stubbing bad weather!", myAgent.getName());
                        response.setContent(getMapper().writeValueAsString(
                            useBadStubData())
                        );
                    } else {
                        response.setContent(getMapper().writeValueAsString(
                            useStubData())
                        );
                    }
                } else {
                    if( new Random().nextInt(100) <= 3) {
                        logger.warn("[{}] Stubbing bad weather!", myAgent.getName());
                        response.setContent(getMapper().writeValueAsString(
                            useBadStubData())
                        );
                    } else {
                        response.setContent(getMapper().writeValueAsString(
                            useApi(requestData))
                        );
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.setConversationId(message.getConversationId());
            logger.info("[{}] Sending message with the weather data for conversation id {}", monitoringAgent.getName(),
                message.getConversationId());
            displayMessageArrow(monitoringAgent, message.getSender());
            monitoringAgent.send(response);
        } else {
            block();
        }
    }

    private MonitoringData useApi(GreenSourceWeatherData requestData) {
        return monitoringAgent.getWeather(requestData);
    }

    private MonitoringData useStubData() {
        return ImmutableMonitoringData.builder()
            .addWeatherData(ImmutableWeatherData.builder()
                .cloudCover(25.0)
                .temperature(25.0)
                .windSpeed(10.0)
                .time(getCurrentTime().toInstant())
                .build())
            .build();
    }

    private MonitoringData useBadStubData() {
        return ImmutableMonitoringData.builder()
            .addWeatherData(ImmutableWeatherData.builder()
                .cloudCover(50.0)
                .temperature(10.0)
                .windSpeed(5.0)
                .time(getCurrentTime().toInstant())
                .build())
            .build();
    }
}
