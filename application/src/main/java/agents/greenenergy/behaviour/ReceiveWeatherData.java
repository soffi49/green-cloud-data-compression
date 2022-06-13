package agents.greenenergy.behaviour;

import static messages.domain.ReplyMessageFactory.prepareReply;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.and;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import messages.domain.SendRefuseProposalMessage;
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

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final MessageTemplate template;
    private final String guid;
    private final ACLMessage cfp;

    public ReceiveWeatherData(GreenEnergyAgent myGreenAgent, final ACLMessage cfp, final String conversationId) {
        this.myGreenEnergyAgent = myGreenAgent;
        this.template = and(MessageTemplate.MatchSender(myGreenAgent.getMonitoringAgent()), MatchConversationId(conversationId));
        this.guid = myGreenEnergyAgent.getName();
        this.cfp = cfp.shallowClone();
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
                    handleRefuse(cfp);
                }
                case ACLMessage.INFORM -> handleInform(data);
            }
        } else {
            block();
        }
    }

    private void handleInform(MonitoringData data) {
        int power = computePower(data);
        logger.info("[{}] Replying with propose message to server.", guid);
        if (power > 0) {
            GreenSourceData responseData = ImmutableGreenSourceData.builder()
                    .pricePerPowerUnit(myGreenEnergyAgent.getPricePerPowerUnit())
                    .availablePowerInTime(power)
                    .job(data.getJob())
                    .build();
            myAgent.addBehaviour(new ProposePowerRequest(myAgent, prepareReply(cfp, responseData, PROPOSE), getDataStore()));
        } else {
            logger.info("[{}] Too bad weather conditions, sending refuse message to server.", guid);
            handleRefuse(cfp);
        }
    }

    private void handleRefuse(final ACLMessage cfp) {
        myAgent.send(SendRefuseProposalMessage.create(cfp).getMessage());
    }

    private int computePower(MonitoringData data) {
        //TODO: implement power computation logic
        return 10;
    }
}
