package agents.cloudnetwork.behaviour.powershortage.handler;

import static common.TimeUtils.getCurrentTime;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Behaviour is responsible for updating internal cloud network state according to the job transfer
 */
public class TransferJobToServer extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(TransferJobToServer.class);

    private final CloudNetworkAgent myCloudNetworkAgent;
    private final String jobId;
    private final AID newServer;

    /**
     * Behaviour constructor.
     *
     * @param myAgent   agent executing the behaviour
     * @param timeout   timeout after which the job should be transferred
     * @param jobId     unique identifier of the job
     * @param newServer server which will take over the job execution
     */
    private TransferJobToServer(Agent myAgent, long timeout, String jobId, AID newServer) {
        super(myAgent, timeout);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.jobId = jobId;
        this.newServer = newServer;
    }

    /**
     * Method creates the behaviour based on the passed arguments
     *
     * @param cloudNetworkAgent cloud network executing the behaviour
     * @param jobId             unique job identifier
     * @param shortageStartTime time when the power shortage starts
     * @param newServer         server which will take over the job execution
     * @return behaviour which transfer the jobs between servers
     */
    public static TransferJobToServer createFor(final CloudNetworkAgent cloudNetworkAgent, final String jobId, final OffsetDateTime shortageStartTime, AID newServer) {
        final long timeDifference = ChronoUnit.MILLIS.between(getCurrentTime(), shortageStartTime);
        final long timeOut = timeDifference < 0 ? 0 : timeDifference;
        return new TransferJobToServer(cloudNetworkAgent, timeOut, jobId, newServer);
    }

    /**
     * Method transfers the job between servers. It is responsible for updating the internal state of the cloud network
     */
    @Override
    protected void onWake() {
        final Job jobToExecute = myCloudNetworkAgent.getJobById(jobId);
        if (Objects.nonNull(jobToExecute)) {
            logger.info("[{}] Updating the internal state of the cloud network", myCloudNetworkAgent.getName());
            myCloudNetworkAgent.getServerForJobMap().replace(jobId, newServer);
        }
    }
}