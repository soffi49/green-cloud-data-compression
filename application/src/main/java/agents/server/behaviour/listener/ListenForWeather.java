package agents.server.behaviour.listener;

import static common.constant.MessageProtocolConstants.SERVER_JOB_START_CHECK_PROTOCOL;
import static common.mapper.JobMapper.mapToJobInstanceId;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.isMessageContentValid;

import agents.server.ServerAgent;
import agents.server.behaviour.StartJobExecution;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.CheckedPowerJob;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenForWeather extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForWeather.class);
    private static final MessageTemplate messageTemplate = and(or(MatchPerformative(INFORM), MatchPerformative(REFUSE)),
        MatchProtocol(SERVER_JOB_START_CHECK_PROTOCOL));

    private final ServerAgent myServerAgent;

    /**
     * Behaviour constructor.
     *
     * @param agent agent that is executing the behaviour
     */
    public ListenForWeather(Agent agent) {
        myServerAgent = (ServerAgent) agent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);
        if (Objects.nonNull(message) && isMessageContentValid(message, CheckedPowerJob.class)) {
            CheckedPowerJob checkedPowerJob = null;
            try {
                checkedPowerJob = getMapper().readValue(message.getContent(), CheckedPowerJob.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            var currentAvailableCapacity = myServerAgent.manage().getAvailableCapacity(checkedPowerJob.getPowerJob().getStartTime(),
                checkedPowerJob.getPowerJob().getEndTime());
            if (currentAvailableCapacity <= 0) {
                //TODO separate change-set
                logger.warn("WTF");
            }

            if (message.getPerformative() == INFORM) {
                logger.info("[{}] Starting job execution!.", myServerAgent.getName());
                myAgent.addBehaviour(
                    StartJobExecution.createFor(
                        myServerAgent,
                        mapToJobInstanceId(checkedPowerJob.getPowerJob()),
                        checkedPowerJob.informCNAStart(),
                        checkedPowerJob.informCNAFinish()));
            } else if (message.getPerformative() == REFUSE) {
                logger.info("[{}] Aborting job execution!.", myServerAgent.getName());
            }
        } else {
            block();
        }
    }

}
