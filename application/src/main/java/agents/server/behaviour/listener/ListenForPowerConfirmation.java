package agents.server.behaviour.listener;

import static common.GUIUtils.announceBookedJob;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import agents.server.behaviour.powercheck.CheckWeatherBeforeJobExecution;
import domain.job.JobInstanceIdentifier;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for listening for confirmation message from Green Energy Source regarding power delivery
 */
public class ListenForPowerConfirmation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForPowerConfirmation.class);

    private MessageTemplate messageTemplate;
    private ServerAgent myServerAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        this.myServerAgent = (ServerAgent) myAgent;
        this.messageTemplate = and(MatchPerformative(INFORM), or(MatchProtocol(SERVER_JOB_CFP_PROTOCOL),
            MatchProtocol(POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL)));
    }

    /**
     * Method listens for the confirmation message coming from Green Energy Source. When the confirmation is received,
     * it schedules the start of job execution
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final JobInstanceIdentifier jobInstanceId = getMapper().readValue(inform.getContent(), JobInstanceIdentifier.class);
                final boolean informCNAStart = inform.getProtocol().equals(SERVER_JOB_CFP_PROTOCOL) || jobInstanceId.getStartTime().isAfter(getCurrentTime());
                if (inform.getProtocol().equals(SERVER_JOB_CFP_PROTOCOL)) {
                    logger.info("[{}] Announcing job {} in network!", myServerAgent.getLocalName(), jobInstanceId.getJobId());
                    announceBookedJob(myServerAgent, jobInstanceId.getJobId());
                }
                logger.info("[{}] Scheduling the execution of the job {}", myAgent.getName(), jobInstanceId.getJobId());
                myAgent.addBehaviour(
                    CheckWeatherBeforeJobExecution.createFor(myServerAgent, jobInstanceId, informCNAStart, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
