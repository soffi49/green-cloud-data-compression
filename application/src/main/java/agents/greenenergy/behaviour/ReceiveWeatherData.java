package agents.greenenergy.behaviour;

import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.GreenSourceData;
import domain.ImmutableGreenSourceData;
import domain.MonitoringData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveWeatherData extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveWeatherData.class);

    private GreenEnergyAgent myGreenEnergyAgent;
    private MessageTemplate template;
    private String guid;

    public ReceiveWeatherData(GreenEnergyAgent myGreenAgent) {
        this.myGreenEnergyAgent = myGreenAgent;
        this.template = MessageTemplate.MatchSender(myGreenAgent.getMonitoringAgent());
        this.guid = myGreenEnergyAgent.getName();
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(template);
        MonitoringData data = null;

        if (nonNull(message)) {
            try {
                data = getMapper().readValue(message.getContent(), MonitoringData.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if (nonNull(data)) {
            switch (message.getPerformative()) {
                case ACLMessage.REFUSE -> {
                    logger.info("[{}] Weather data not available, sending refuse message to server.", guid);
                    handleRefuse(data, "Refuse: weather data not available");
                }
                case ACLMessage.INFORM -> handleInform(data);
            }
        } else {
            block();
        }
    }

    private void handleInform(MonitoringData data) {
        int power = computePower(data);
        if (power > 0) {
            GreenSourceData responseData = ImmutableGreenSourceData.builder()
                .pricePerPowerUnit(myGreenEnergyAgent.getPricePerPowerUnit())
                .availablePowerInTime(power)
                .job(data.getJob())
                .build();
            getParent().getDataStore().put(data.getJob().getJobId(), responseData);
        } else {
            logger.info("[{}] Too bad weather conditions, sending refuse message to server.", guid);
            handleRefuse(data, "Refuse: too bad weather conditions ");
        }
    }

    private void handleRefuse(MonitoringData data, String message) {
        getParent().getDataStore().put(data.getJob().getJobId(), message);
    }

    private int computePower(MonitoringData data) {
        //TODO: implement power computation logic
        return 10;
    }
}
