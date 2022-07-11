package agents.cloudnetwork.behaviour.powershortage.handler;

import static common.TimeUtils.getCurrentTime;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Date;
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
     * @param myAgent      agent executing the behaviour
     * @param transferTime time when the power shortage starts
     * @param jobId        unique identifier of the job
     * @param newServer    server which will take over the job execution
     */
    private TransferJobToServer(Agent myAgent, Date transferTime, String jobId, AID newServer) {
        super(myAgent, transferTime);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.jobId = jobId;
        this.newServer = newServer;
    }

    /**
     * Method creates the behaviour based on the passed arguments
     *
     * @param cloudNetworkAgent cloud network executing the behaviour
     * @param powerShortageJob  job to be transferred
     * @param newServer         server which will take over the job execution
     * @return behaviour which transfer the jobs between servers
     */
    public static TransferJobToServer createFor(final CloudNetworkAgent cloudNetworkAgent, final PowerShortageJob powerShortageJob, AID newServer) {
        final OffsetDateTime transferTime = getCurrentTime().isAfter(powerShortageJob.getPowerShortageStart()) ? getCurrentTime() : powerShortageJob.getPowerShortageStart();
        return new TransferJobToServer(cloudNetworkAgent, Date.from(transferTime.toInstant()), powerShortageJob.getJobInstanceId().getJobId(), newServer);
    }

    /**
     * Method transfers the job between servers. It is responsible for updating the internal state of the cloud network
     */
    @Override
    protected void onWake() {
        final Job jobToExecute = myCloudNetworkAgent.manage().getJobById(jobId);
        if (Objects.nonNull(jobToExecute)) {
            logger.info("[{}] Updating the internal state of the cloud network", myCloudNetworkAgent.getName());
            myCloudNetworkAgent.getServerForJobMap().replace(jobId, newServer);
        }
    }
}