package agents.greenenergy.behaviour;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenForFinishedJobs extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForFinishedJobs.class);

    private final MessageTemplate messageTemplate;
    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    private ListenForFinishedJobs(final GreenEnergyAgent myGreenEnergyAgent) {
        this.myGreenEnergyAgent = myGreenEnergyAgent;
        this.messageTemplate = MessageTemplate.and(MatchPerformative(INFORM), MatchProtocol(FINISH_JOB_PROTOCOL));
        this.guid = myGreenEnergyAgent.getName();
    }

    public static ListenForFinishedJobs createFor(final GreenEnergyAgent greenEnergyAgent) {
        return new ListenForFinishedJobs(greenEnergyAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myGreenEnergyAgent.receive(messageTemplate);

        if (nonNull(message)) {
            Job job = readJob(message);
            if (nonNull(job)) {
                myGreenEnergyAgent.getCurrentJobs().remove(job);
                myGreenEnergyAgent.setAvailableCapacity(myGreenEnergyAgent.getAvailableCapacity() + job.getPower());
                logger.info("[{}] Finish the execution of the job with id {} for client with id {}", guid,
                            job.getJobId(), job.getClientIdentifier());
            }
        } else {
            block();
        }
    }

    private Job readJob(ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), Job.class);
        } catch (JsonProcessingException e) {
            logger.info("[{}] Received invalid information about finished job", guid);
        }
        return null;
    }
}
