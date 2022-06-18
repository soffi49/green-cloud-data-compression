package agents.cloudnetwork.behaviour;

import static messages.domain.JobStatusMessageFactory.prepareDelayMessageForClient;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour which is responsible for passing to the client the information that the job execution has some delay.
 */
public class ListenForJobDelay extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobDelay.class);

    private final String jobId;
    private final CloudNetworkAgent myCloudNetworkAgent;

    /**
     * Behaviour constructor.
     *
     * @param agent   agent which is executing the behaviour
     * @param timeout timeout after which the job should start the execution
     * @param jobId   unique job identifier
     */
    public ListenForJobDelay(Agent agent, long timeout, String jobId) {
        super(agent, timeout);
        this.myCloudNetworkAgent = (CloudNetworkAgent) agent;
        this.jobId = jobId;
    }

    /**
     * Method verifies if the job execution has started at the correct time. If there is some delay - it informs the
     * client about it.
     */
    @Override
    protected void onWake() {
        final Job job = myCloudNetworkAgent.getJobById(jobId);
        if (Objects.nonNull(myCloudNetworkAgent.getNetworkJobs().get(job)) && !myCloudNetworkAgent.getNetworkJobs().get(job).equals(JobStatusEnum.IN_PROGRESS)) {
            logger.error("[{}] There is no message regarding the job start. Sending delay information", myAgent.getName());
            myAgent.send(prepareDelayMessageForClient(jobId, myCloudNetworkAgent.getJobById(jobId).getClientIdentifier()));
            //TODO here we can pass another behaviour handling what will happen if the message won't come at all! :)
        }
    }
}
