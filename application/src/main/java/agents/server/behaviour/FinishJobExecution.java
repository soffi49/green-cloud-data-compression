package agents.server.behaviour;

import agents.client.message.SendJobMessage;
import agents.server.ServerAgent;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Behaviour responsible for returning to the CNA and GreenSource the information that the job execution has finished
 * (instead of HandleServerCNAInformJobDone)
 */
public class FinishJobExecution extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(FinishJobExecution.class);

    private ServerAgent serverAgent;
    private Job jobToExecute;


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
        serverAgent = (ServerAgent) myAgent;
    }

    @Override
    protected void onWake() {
        logger.info("[{}] Finished executing the job for {}", myAgent, jobToExecute.getClientIdentifier());
        serverAgent.getCurrentJobs().remove(jobToExecute);
        serverAgent.setPowerInUse(serverAgent.getPowerInUse() - jobToExecute.getPower());
        final ACLMessage informMessage = SendJobMessage.create(jobToExecute,
                                           List.of(serverAgent.getGreenSourceForJobMap().get(jobToExecute), serverAgent.getOwnerCloudNetworkAgent()),
                                           ACLMessage.INFORM).getMessage();
        informMessage.setConversationId("FINISHED");
        myAgent.send(informMessage);
        serverAgent.getGreenSourceForJobMap().remove(jobToExecute);
    }
}
