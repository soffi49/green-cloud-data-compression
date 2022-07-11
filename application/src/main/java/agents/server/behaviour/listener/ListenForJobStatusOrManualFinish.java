package agents.server.behaviour.listener;

import static common.constant.MessageProtocolConstants.JOB_START_STATUS_PROTOCOL;
import static common.constant.MessageProtocolConstants.MANUAL_JOB_FINISH_PROTOCOL;
import static domain.job.JobStatusEnum.JOB_IN_PROGRESS;
import static jade.lang.acl.ACLMessage.AGREE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for listening for the requests for some job start status or manual finish inform
 */
public class ListenForJobStatusOrManualFinish extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobStatusOrManualFinish.class);

    private static final MessageTemplate messageTemplate = or(
        and(MatchPerformative(REQUEST), MatchProtocol(JOB_START_STATUS_PROTOCOL)),
        and(MatchPerformative(INFORM), MatchProtocol(MANUAL_JOB_FINISH_PROTOCOL))
    );
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
     * Method listens for the messages.
     * - coming from the Cloud Network, it verifies the status of the requested job and sends back that information,
     * - coming from the Green Cloud Network, informing that the job had to be finished manually because the time
     *   after which the information about job being finished has passed
     **/
    @Override
    public void action() {
        final ACLMessage request = myAgent.receive(messageTemplate);

        if (Objects.nonNull(request) && request.getProtocol().equals(JOB_START_STATUS_PROTOCOL)) {
            try {
                final String jobId = request.getContent();
                logger.info("[{}] Received request to verify job start status {}", myAgent.getName(), jobId);
                final Map.Entry<Job, JobStatusEnum> jobInstance = myServerAgent.manage().getCurrentJobInstance(jobId);
                final ACLMessage reply = request.createReply();
                if(Objects.nonNull(jobInstance) && JOB_IN_PROGRESS.contains(jobInstance.getValue())) {
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
        } else if(Objects.nonNull(request) && request.getProtocol().equals(MANUAL_JOB_FINISH_PROTOCOL)) {
            try {
                Job job = null;
                try {
                    final String jobId = getMapper().readValue(request.getContent(), String.class);
                    job = myServerAgent.manage().getJobById(jobId);
                } catch (MismatchedInputException e) {
                    final JobInstanceIdentifier identifier = getMapper().readValue(request.getContent(), JobInstanceIdentifier.class);
                    job = myServerAgent.manage().getJobByIdAndStartDate(identifier);
                }
                if (Objects.nonNull(myServerAgent.getServerJobs().get(job)) && myServerAgent.getServerJobs().get(job).equals(JobStatusEnum.IN_PROGRESS)) {
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