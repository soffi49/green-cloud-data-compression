package agents.cloudnetwork.behaviour.jobstatus;

import static common.constant.MessageProtocolConstants.*;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareJobFailureMessageForClient;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.JobInstanceIdentifier;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for returning to the client
 * information that the job execution has failed
 */
public class ReturnJobFailure extends CyclicBehaviour {
    private static final Logger logger = LoggerFactory.getLogger(ReturnJobFailure.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(FAILURE),
            MatchProtocol(FAILED_JOB_PROTOCOL));

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
     * Method which listens for the failure regarding the job.
     * It passes that information to the client.
     */
    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                final JobInstanceIdentifier jobInstanceId = getMapper().readValue(message.getContent(),
                        JobInstanceIdentifier.class);
                if (Objects.nonNull(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()))) {
                        logger.info("[{}] Sending information that the job {} execution has failed",
                                myAgent.getName(), jobInstanceId.getJobId());
                        final String clientId = myCloudNetworkAgent
                                .manage()
                                .getJobById(jobInstanceId.getJobId())
                                .getClientIdentifier();
                        myCloudNetworkAgent
                                .getNetworkJobs()
                                .remove(myCloudNetworkAgent.manage().getJobById(jobInstanceId.getJobId()));
                        myCloudNetworkAgent
                                .getServerForJobMap().remove(jobInstanceId.getJobId());
                        myAgent.send(prepareJobFailureMessageForClient(clientId, FAILED_JOB_PROTOCOL));
                    }
                }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
