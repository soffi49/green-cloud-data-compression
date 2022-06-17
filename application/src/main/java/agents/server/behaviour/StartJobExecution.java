package agents.server.behaviour;

import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for starting the job execution
 */
public class StartJobExecution extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(StartJobExecution.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(SERVER_JOB_CFP_PROTOCOL));

    private ServerAgent myServerAgent;

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    /**
     * Method run
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final String jobId = getMapper().readValue(inform.getContent(), String.class);
                final Job job = myServerAgent.getJobById(jobId);
                logger.info("[{}] Starting the execution of the job", myAgent.getName());

                myServerAgent.getServerJobs().replace(job, JobStatusEnum.IN_PROGRESS);
                myAgent.addBehaviour(FinishJobExecution.createFor(myServerAgent, job));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
