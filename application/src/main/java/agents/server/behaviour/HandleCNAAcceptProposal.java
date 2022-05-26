package agents.server.behaviour;

import agents.client.message.SendJobMessage;
import agents.server.ServerAgent;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

public class HandleCNAAcceptProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleCNAAcceptProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACCEPT_PROPOSAL);

    private ServerAgent serverAgent;

    private HandleCNAAcceptProposal(final ServerAgent serverAgent) {
        super(serverAgent);
    }

    public static HandleCNAAcceptProposal createFor(final ServerAgent serverAgent) {
        return new HandleCNAAcceptProposal(serverAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        serverAgent = (ServerAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                logger.info("[{}] Sending accept proposal to green source agent", myAgent);
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                final AID greenSourceForJob = serverAgent.getGreenSourceForJobMap().get(job);
                serverAgent.getCurrentJobs().add(job);
                serverAgent.setPowerInUse(serverAgent.getPowerInUse() + job.getPower());
                myAgent.send(SendJobMessage.create(job, List.of(greenSourceForJob), ACCEPT_PROPOSAL).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
