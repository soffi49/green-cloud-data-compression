package agents.cloudnetwork.behaviour;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REQUEST;
import static mapper.JsonMapper.getMapper;

import agents.client.message.SendJobMessage;
import agents.cloudnetwork.CloudNetworkAgent;
import domain.ImmutableCloudNetworkData;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudNetworkAgentReadMessages extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(CloudNetworkAgentReadMessages.class);

    private CloudNetworkAgent cloudNetworkAgent;

    public CloudNetworkAgentReadMessages(CloudNetworkAgent cloudNetworkAgent) {
        this.cloudNetworkAgent = cloudNetworkAgent;
    }

    public static CloudNetworkAgentReadMessages createFor(CloudNetworkAgent cloudNetworkAgent) {
        return new CloudNetworkAgentReadMessages(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = cloudNetworkAgent.receive();

        if (Objects.nonNull(message)) {
            switch (message.getPerformative()) {
                case PROPOSE:
                    final ACLMessage respond = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    try {
                        var data = ImmutableCloudNetworkData.builder()
                            .inUsePower(cloudNetworkAgent.getInUsePower())
                            .jobsCount(cloudNetworkAgent.getJobsCount())
                            .build();
                        respond.setContent(getMapper().writeValueAsString(data));
                        logger.info("{} Accepting proposal", myAgent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    respond.addReceiver(message.getSender());
                    cloudNetworkAgent.send(respond);
                    break;
                case REQUEST:
                    cloudNetworkAgent.setJobsCount(cloudNetworkAgent.getJobsCount() + 1);
                    try {
                        final Job job = getMapper().readValue(message.getContent(), Job.class);
                        cloudNetworkAgent.getCurrentJobs().add(job);
                        cloudNetworkAgent.setInUsePower(cloudNetworkAgent.getInUsePower() + job.getPower());
                        myAgent.send(
                            SendJobMessage.create(job, cloudNetworkAgent.getServiceAgentList(), PROPOSE).getMessage());
                        logger.info("{} Sending proposal to server", myAgent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } else {
            block();
        }
    }
}
