package agents.server.behaviour;

import static common.constant.MessageProtocolConstants.MANUAL_JOB_FINISH_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for listening for the unfinished job message coming from the green source agent
 */
public class ListenForUnfinishedJobInformation extends CyclicBehaviour {
    private static final Logger logger = LoggerFactory.getLogger(ListenForUnfinishedJobInformation.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(MANUAL_JOB_FINISH_PROTOCOL));

    private ServerAgent myServerAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    /**
     * Method listens for the message coming from Green Source informing that the job should be finished by now.
     * Then, it finishes the job execution and sends the corresponding information to Cloud Network
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                Job job;
                try {
                    final String jobId = getMapper().readValue(inform.getContent(), String.class);
                    job = myServerAgent.manage().getJobById(jobId);
                } catch (MismatchedInputException e) {
                    final JobInstanceIdentifier identifier = getMapper().readValue(inform.getContent(), JobInstanceIdentifier.class);
                    job = myServerAgent.manage().getJobByIdAndStartDate(identifier);
                }if (Objects.nonNull(myServerAgent.getServerJobs().get(job)) && myServerAgent.getServerJobs().get(job).equals(JobStatusEnum.IN_PROGRESS)) {
                    logger.debug("[{}] Information about finishing job with id {} does not reach the green source", myAgent.getName(), job.getClientIdentifier());
                    logger.info("[{}] Finished executing the job for {}", myAgent.getName(), job.getClientIdentifier());
                    myServerAgent.manage().finishJobExecution(job, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
