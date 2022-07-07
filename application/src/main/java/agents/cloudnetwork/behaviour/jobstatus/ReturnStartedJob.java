package agents.cloudnetwork.behaviour.jobstatus;

import static common.GUIUtils.announceStartedJob;
import static common.constant.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareStartMessageForClient;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for receiving information that the execution of the job has started
 */
public class ReturnStartedJob extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReturnStartedJob.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(STARTED_JOB_PROTOCOL));

    private CloudNetworkAgent myCloudNetworkAgent;

    /**
     * Method runs at the behaviour start. It casts the abstract agent to the agent of type CloudNetworkAgent
     */
    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    /**
     * Method which listens for the information that some job execution has started. It finds the corresponding job
     * in network data, updates the network state and passes the started job message to the appropriate client.
     */
    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                final JobInstanceIdentifier jobInstanceId = getMapper().readValue(message.getContent(), JobInstanceIdentifier.class);
                if (Objects.nonNull(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()))) {
                    logger.info("[{}] Sending information that the job {} execution has started", myAgent.getName(), jobInstanceId.getJobId());
                    myCloudNetworkAgent.getNetworkJobs().replace(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()), JobStatusEnum.IN_PROGRESS);
                    announceStartedJob(myCloudNetworkAgent);
                    myAgent.send(prepareStartMessageForClient(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()).getClientIdentifier()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
