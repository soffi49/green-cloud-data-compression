package agents.client.behaviour;

import agents.client.ClientAgent;
import domain.Job;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import java.util.List;
import message.client.ProposeJobMessage;

/**
 * One shot behaviour for client agent. Its purpose is to
 * announce job over the Cloud Network Agents
 *
 */
public class SendJobRequest extends OneShotBehaviour {

    private final ClientAgent clientAgent;
    private final Job job;

    private SendJobRequest(ClientAgent clientAgent, Job job) {
        this.clientAgent = clientAgent;
        this.job = job;
    }

    public static SendJobRequest createFor(ClientAgent clientAgent, Job job) {
        return new SendJobRequest(clientAgent, job);
    }

    @Override
    public void action() {
        final List<AID> agentsCNA = clientAgent.getCNAAgentList(myAgent);
        agentsCNA.forEach(agent -> {
            myAgent.send(ProposeJobMessage.create(job, agent).getMessage());
        });
    }
}
