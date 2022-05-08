package agents.client.behaviour;

import agents.client.ClientAgent;
import agents.cloudnetwork.AbstractCloudNetworkAgent;
import domain.CloudNetworkData;
import domain.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import message.client.SendJobMessage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.*;

/**
 * Cyclic behavior for client agent. Its purpose is to
 * read and handle incoming messages
 *
 */
public class ClientAgentReadMessages extends CyclicBehaviour {

    private final ClientAgent clientAgent;
    private final Job job;

    private ClientAgentReadMessages(ClientAgent clientAgent, Job job) {
        this.clientAgent = clientAgent;
        this.job = job;
    }

    public static ClientAgentReadMessages createFor(ClientAgent clientAgent, Job job) {
        return new ClientAgentReadMessages(clientAgent, job);
    }

    @Override
    public void action() {
        final ACLMessage message = clientAgent.receive();

        if (Objects.nonNull(message)) {

            if(Objects.isNull(clientAgent.getChosenCloudNetworkAgent())) {
                clientAgent.setResponsesReceivedCount(clientAgent.getResponsesReceivedCount() + 1);
            }

            switch (message.getPerformative()) {
                case AGREE:
                    if (clientAgent.getResponsesReceivedCount() < clientAgent.getMessagesSentCount()) {
                        try {
                            clientAgent.getCloudNetworkAgentList().put(message.getSender(), (CloudNetworkData) message.getContentObject());
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                    } else {
                        clientAgent.setChosenCloudNetworkAgent(chooseCNAToExecuteJob());
                        myAgent.send(SendJobMessage.create(job, List.of(clientAgent.getChosenCloudNetworkAgent()), PROPOSE).getMessage());
                    }
                    break;
            }
        } else {
            block();
        }
    }

    private AID chooseCNAToExecuteJob() {
        final Comparator<Map.Entry<AID, CloudNetworkData>> compareCNA =
                Comparator.comparingInt(cna -> cna.getValue().getJobsCount());
        return clientAgent.getCloudNetworkAgentList().entrySet().stream().min(compareCNA).orElseThrow().getKey();
    }
}
