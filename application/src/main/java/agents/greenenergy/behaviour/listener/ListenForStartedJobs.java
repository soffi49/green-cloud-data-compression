package agents.greenenergy.behaviour.listener;

import static common.GUIUtils.updateGreenSourceState;
import static common.constant.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static domain.job.JobStatusEnum.IN_PROGRESS;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobInstanceIdentifier;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Behaviour which listens for the information that the execution of the given job starts.
 */
public class ListenForStartedJobs extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForStartedJobs.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.and(MatchPerformative(INFORM), MatchProtocol(STARTED_JOB_PROTOCOL));

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myGreenEnergyAgent agent which is executing the behaviour
     */
    public ListenForStartedJobs(final GreenEnergyAgent myGreenEnergyAgent) {
        this.myGreenEnergyAgent = myGreenEnergyAgent;
        this.guid = myGreenEnergyAgent.getName();
    }

    /**
     * Method which listens for the information that the job execution has started. It is responsible
     * for updating the current green energy source state.
     */
    @Override
    public void action() {
        final ACLMessage message = myGreenEnergyAgent.receive(messageTemplate);
        if (nonNull(message)) {
            try {
                final JobInstanceIdentifier jobInstanceId = getMapper().readValue(message.getContent(), JobInstanceIdentifier.class);
                if (nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobInstanceId))) {
                    myGreenEnergyAgent.getPowerJobs().replace(myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobInstanceId), IN_PROGRESS);
                    logger.info("[{}] Started the execution of the job with id {}", guid, jobInstanceId.getJobId());
                    updateGreenSourceState(myGreenEnergyAgent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}