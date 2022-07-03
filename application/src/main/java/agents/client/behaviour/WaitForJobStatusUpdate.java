package agents.client.behaviour;

import static agents.client.ClientAgentConstants.MAX_TIME_DIFFERENCE;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.DELAYED_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;

import agents.client.ClientAgent;
import com.gui.domain.nodes.ClientAgentNode;
import com.gui.domain.types.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Behaviour which handles the information that the job execution is done
 */
public class WaitForJobStatusUpdate extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(WaitForJobStatusUpdate.class);
    private static final MessageTemplate messageTemplate = and(or(MatchProtocol(FINISH_JOB_PROTOCOL), MatchProtocol(DELAYED_JOB_PROTOCOL)),
                                                                  MatchPerformative(INFORM));

    private final ClientAgent myClientAgent;

    /**
     * Behaviours constructor.
     *
     * @param clientAgent agent executing the behaviour
     */
    public WaitForJobStatusUpdate(final ClientAgent clientAgent) {
        super(clientAgent);
        this.myClientAgent = clientAgent;
    }

    /**
     * Method which waits for messages informing about changes in the job's status
     */
    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);
        if (Objects.nonNull(message)) {
            switch (message.getProtocol()){
                case FINISH_JOB_PROTOCOL -> {
                    checkIfJobFinishedOnTime();
                    ((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.FINISHED);
                    myClientAgent.getGuiController().updateClientsCountByValue(-1);
                }
                case DELAYED_JOB_PROTOCOL ->  {
                    logger.info("[{}] The execution of my job has some delay! :(", myAgent.getName());
                    ((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.DELAYED);
                }
            }
        } else {
            block();
        }
    }

    private void checkIfJobFinishedOnTime() {
        final OffsetDateTime endTime = getCurrentTime();
        final long timeDifference = ChronoUnit.MILLIS.between(endTime, myClientAgent.getSimulatedJobEnd());
        if (MAX_TIME_DIFFERENCE.isValidValue(timeDifference)) {
            logger.info("[{}] The execution of my job finished on time! :)", myAgent.getName());
        } else {
            logger.info("[{}] The execution of my job finished with a delay equal to {}! :(", myAgent.getName(), timeDifference);
        }
    }
}
