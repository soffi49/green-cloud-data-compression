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
import static jade.lang.acl.ACLMessage.INFORM;
import static mapper.JsonMapper.getMapper;

public class HandleGreenSourceJobInform extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleGreenSourceJobInform.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(INFORM);

    private ServerAgent serverAgent;

    private HandleGreenSourceJobInform(final ServerAgent serverAgent) {
        super(serverAgent);
    }

    public static HandleGreenSourceJobInform createFor(final ServerAgent serverAgent) {
        return new HandleGreenSourceJobInform(serverAgent);
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
                logger.info("[{}] Starting the execution of the job", myAgent);
                serverAgent.getCurrentJobs().add(job);
                serverAgent.setPowerInUse(serverAgent.getPowerInUse() + job.getPower());
                myAgent.addBehaviour(FinishJobExecution.createFor(serverAgent, job));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

}
