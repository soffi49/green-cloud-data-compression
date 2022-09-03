package agents.greenenergy.behaviour.powercheck;

import static utils.TimeUtils.getCurrentTime;
import static messages.domain.constants.MessageProtocolConstants.ON_HOLD_JOB_CHECK_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.factory.PowerShortageMessageFactory.preparePowerShortageFinishInformation;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.mapper.JobMapper;
import domain.MonitoringData;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for receiving and handling the weather data requested for the given job
 */
public class ReceiveForecastData extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveForecastData.class);

    private final MessageTemplate messageTemplate;
    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;
    private final PowerJob powerJob;
    private final SequentialBehaviour parentBehaviour;
    private final String protocol;

    /**
     * Behaviour constructor.
     *
     * @param myGreenAgent    agent which is executing the behaviour
     * @param powerJob        job of interest
     * @param protocol        message protocol
     * @param conversationId  message conversation id
     * @param parentBehaviour behaviour which should be removed
     */
    public ReceiveForecastData(GreenEnergyAgent myGreenAgent, PowerJob powerJob, String protocol, String conversationId, SequentialBehaviour parentBehaviour) {
        this.myGreenEnergyAgent = myGreenAgent;
        this.messageTemplate = and(and(MatchProtocol(protocol), MatchSender(myGreenAgent.getMonitoringAgent())),
                                   and(or(MatchPerformative(INFORM), MatchPerformative(REFUSE)), MatchConversationId(conversationId)));
        this.guid = myGreenEnergyAgent.getName();
        this.powerJob = powerJob;
        this.parentBehaviour = parentBehaviour;
        this.protocol = protocol;
    }

    /**
     * Method responsible for listening for the Monitoring Agent reply. It waits for the reply, then processes the
     * received weather information and handles the response using method assign to given protocol
     */
    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);
        if (nonNull(message)) {
            try {
                final MonitoringData data = getMapper().readValue(message.getContent(), MonitoringData.class);
                if (nonNull(data)) {
                    switch (message.getPerformative()) {
                        case ACLMessage.REFUSE -> handleRefuse();
                        case INFORM -> handleInform(data);
                    }
                }
            } catch (JsonProcessingException e) {
                logger.info("[{}] I didn't understand the response with the weather data, sending refuse message to server", myGreenEnergyAgent.getName());
            }
            myAgent.removeBehaviour(parentBehaviour);
        } else {
            block();
        }
    }

    private void handleInform(final MonitoringData data) {
        if (protocol.equals(ON_HOLD_JOB_CHECK_PROTOCOL)) {
            double availablePower = myGreenEnergyAgent.manage().getAverageAvailablePowerCheckForJobOnHold(powerJob, data).orElse(0.0);
            if (availablePower <= 0.0) {
                logger.info("[{}] There is not enough available power to put job back in progress. Leaving the job {} on hold", guid, powerJob.getJobId());
            } else {
                logger.info("[{}] Changing the status of the job {}", myGreenEnergyAgent.getLocalName(), powerJob.getJobId());
                final JobStatusEnum newStatus = powerJob.getStartTime().isAfter(getCurrentTime()) ? JobStatusEnum.ACCEPTED : JobStatusEnum.IN_PROGRESS;
                myGreenEnergyAgent.getPowerJobs().replace(powerJob, newStatus);
                myGreenEnergyAgent.manage().updateGreenSourceGUI();
                myGreenEnergyAgent.send(preparePowerShortageFinishInformation(JobMapper.mapToJobInstanceId(powerJob), myGreenEnergyAgent.getOwnerServer()));
            }
        }
    }

    private void handleRefuse() {
        if (protocol.equals(ON_HOLD_JOB_CHECK_PROTOCOL)) {
            logger.info("[{}] The data for the job is not available. Leaving job {} on hold", guid, powerJob.getJobId());
        }
    }
}
