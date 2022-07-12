package agents.cloudnetwork.behaviour;

import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleReceiveJobRequestRetry extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleReceiveJobRequestRetry.class);

    private final ACLMessage originalMessage;
    private final String jobId;

    public ScheduleReceiveJobRequestRetry(Agent agent, long timeout, ACLMessage originalMessage, String jobId) {
        super(agent, timeout);
        this.originalMessage = originalMessage;
        this.jobId = jobId;
    }

    @Override
    protected void onWake() {
        myAgent.send(originalMessage);
        logger.info("Retrying to process a job with id {}", jobId);
        myAgent.removeBehaviour(this);
    }
}
