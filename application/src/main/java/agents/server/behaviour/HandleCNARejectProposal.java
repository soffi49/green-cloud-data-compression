package agents.server.behaviour;

import common.message.SendJobMessage;
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

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

public class HandleCNARejectProposal extends CyclicBehaviour {
    private static final Logger logger = LoggerFactory.getLogger(HandleCNARejectProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(REJECT_PROPOSAL);

    private ServerAgent serverAgent;

    private HandleCNARejectProposal(final ServerAgent serverAgent) {
        super(serverAgent);
    }

    public static HandleCNARejectProposal createFor(final ServerAgent serverAgent) {
        return new HandleCNARejectProposal(serverAgent);
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
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                logger.info("[{}] Cloud Network rejected the job proposal", myAgent);
                final AID greenSourceToReject = serverAgent.getGreenSourceForJobMap().get(job);
                serverAgent.getGreenSourceForJobMap().remove(job);
                serverAgent.send(SendJobMessage.create(job, List.of(greenSourceToReject), REJECT_PROPOSAL).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
