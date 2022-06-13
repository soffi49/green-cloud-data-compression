package agents.server.behaviour;

import static common.constant.MessageProtocolConstants.FINISH_JOB_PROTOCOL;

import agents.server.ServerAgent;
import messages.domain.SendJobMessage;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Behaviour responsible for returning to the CNA and GreenSource the information that the job execution has finished
 */
public class FinishJobExecution extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FinishJobExecution.class);
    private final Job jobToExecute;
    private ServerAgent myServerAgent;

    private FinishJobExecution(Agent a, long timeOut, final Job job) {
        super(a, timeOut);
        this.jobToExecute = job;
    }

    public static FinishJobExecution createFor(final ServerAgent serverAgent, final Job jobToExecute) {
        final long timeOut = ChronoUnit.HOURS.between(jobToExecute.getStartTime(), jobToExecute.getEndTime()) * 2 * 1000;  // in our simulation 1h = 2s so 2000ms
        return new FinishJobExecution(serverAgent, timeOut, jobToExecute);
    }

    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    @Override
    protected void onWake() {
        logger.info("[{}] Finished executing the job for {}", myAgent.getName(), jobToExecute.getClientIdentifier());
        final ACLMessage informMessage = SendJobMessage.create(jobToExecute,
                                                               List.of(myServerAgent.getGreenSourceForJobMap().get(jobToExecute),
                                                                       myServerAgent.getOwnerCloudNetworkAgent()),
                                                               ACLMessage.INFORM).getMessage();
        informMessage.setProtocol(FINISH_JOB_PROTOCOL);
        updateNetworkInformation();
        myAgent.send(informMessage);
    }

    private void updateNetworkInformation() {
        myServerAgent.getCurrentJobs().remove(jobToExecute);
        myServerAgent.setPowerInUse(myServerAgent.getPowerInUse() - jobToExecute.getPower());
        myServerAgent.getGreenSourceForJobMap().remove(jobToExecute);
    }
}
