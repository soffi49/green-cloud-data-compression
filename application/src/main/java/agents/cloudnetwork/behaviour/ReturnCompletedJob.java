package agents.cloudnetwork.behaviour;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.message.SendJobFinishedMessage;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for returning to the client the information that the job execution has finished
 */
public class ReturnCompletedJob extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReturnCompletedJob.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(FINISH_JOB_PROTOCOL));

    private CloudNetworkAgent myCloudNetworkAgent;

    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                logger.info("[{}] Sending information that the job execution is finished", myAgent);
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                updateNetworkInformation(job);
                myAgent.send(SendJobFinishedMessage.create(job).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private void updateNetworkInformation(final Job job) {
        myCloudNetworkAgent.getCurrentJobs().remove(job);
        myCloudNetworkAgent.getServerForJobMap().remove(job);
        myCloudNetworkAgent.setInUsePower(myCloudNetworkAgent.getInUsePower() - job.getPower());
        myCloudNetworkAgent.setJobsCount(myCloudNetworkAgent.getJobsCount() - 1);
    }
}
