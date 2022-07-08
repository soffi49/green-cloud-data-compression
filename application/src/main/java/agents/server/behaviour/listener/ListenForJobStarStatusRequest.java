package agents.server.behaviour.listener;

import static common.constant.MessageProtocolConstants.JOB_START_STATUS_PROTOCOL;
import static domain.job.JobStatusEnum.JOB_IN_PROGRESS;
import static jade.lang.acl.ACLMessage.*;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * Behaviour is responsible for listening for the requests for some job start status
 */
public class ListenForJobStarStatusRequest extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobStarStatusRequest.class);

    private static final MessageTemplate messageTemplate = and(MatchPerformative(REQUEST), MatchProtocol(JOB_START_STATUS_PROTOCOL));
    private ServerAgent myServerAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        this.myServerAgent = (ServerAgent) myAgent;
    }

    /**
     * Method listens for the requests coming from the Cloud Network. Then it verifies the status of the requested job
     * and sends back that information.
     */
    @Override
    public void action() {
        final ACLMessage request = myAgent.receive(messageTemplate);

        if (Objects.nonNull(request)) {
            try {
                final String jobId = request.getContent();
                logger.info("[{}] Received request to verify job start status {}", myAgent.getName(), jobId);
                final Map.Entry<Job, JobStatusEnum> jobInstance = myServerAgent.manage().getCurrentJobInstance(jobId);
                final ACLMessage reply = request.createReply();
                if(JOB_IN_PROGRESS.contains(jobInstance.getValue())) {
                    reply.setContent("JOB STARTED");
                    reply.setPerformative(AGREE);
                } else {
                    reply.setContent("JOB HAS NOT STARTED");
                    reply.setPerformative(REFUSE);
                }
                myServerAgent.send(reply);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}