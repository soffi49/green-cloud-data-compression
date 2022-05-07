package agents.cloudnetwork.behaviour;

import static common.CommonUtils.sendJobRequestToAgents;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REQUEST;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.Objects;

public class CloudNetworkAgentCyclicBehaviour extends CyclicBehaviour {

    private CloudNetworkAgent cloudNetworkAgent;

    public CloudNetworkAgentCyclicBehaviour(CloudNetworkAgent cloudNetworkAgent) {
        this.cloudNetworkAgent = cloudNetworkAgent;
    }

    public static CloudNetworkAgentCyclicBehaviour createFor(CloudNetworkAgent cloudNetworkAgent) {
        return new CloudNetworkAgentCyclicBehaviour(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = cloudNetworkAgent.receive();
        if (Objects.nonNull(message)) {
            switch (message.getPerformative()) {
                case REQUEST:
                    final ACLMessage respond = new ACLMessage(ACLMessage.AGREE);
                    //respond.setContentObject(cloudNetworkData);
                    respond.addReceiver(message.getSender());
                    cloudNetworkAgent.send(respond);
                    break;
                case PROPOSE:
                    //cloudNetworkData.setJobsCount(cloudNetworkData.getJobsCount() + 1);
                    try {
                        final Job newJob = (Job) message.getContentObject();
                        //cloudNetworkData.getCurrentJobs().add(newJob);
                        //cloudNetworkData.setInUsePower(cloudNetworkData.getInUsePower() + newJob.getPower());
                        //sendJobRequestToAgents(myAgent, saAgentList, newJob);
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }

            }
        } else {
            block();
        }
    }
}
