package agents.server.behaviour;

import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for starting the job execution
 */
public class StartJobExecution extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(StartJobExecution.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(SERVER_JOB_CFP_PROTOCOL));

    private ServerAgent myServerAgent;

    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final Job job = getMapper().readValue(inform.getContent(), Job.class);
                logger.info("[{}] Starting the execution of the job", myAgent.getName());

                updateNetworkInformation(job);
                myAgent.addBehaviour(FinishJobExecution.createFor(myServerAgent, job));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private void updateNetworkInformation(final Job job) {
        myServerAgent.getCurrentJobs().add(job);
        myServerAgent.setPowerInUse(myServerAgent.getPowerInUse() + job.getPower());
    }
}
