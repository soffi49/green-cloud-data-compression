package agents.server.behaviour.powershortage.transfer;

import static java.util.Objects.nonNull;

import agents.server.ServerAgent;
import domain.job.JobStatusEnum;
import domain.job.PowerShortageJob;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for retrying to transfer the job which was put on hold
 */
public class RetryJobTransferRequestInCloudNetwork extends WakerBehaviour {


    private static final Logger logger = LoggerFactory.getLogger(RetryJobTransferRequestInCloudNetwork.class);

    private final ServerAgent myServerAgent;
    private final PowerShortageJob jobToTransfer;
    private final Behaviour requestBehaviour;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param agent            agent running the behaviour
     * @param timeout          time after which the behaviour should execute
     * @param jobToTransfer    job for which the transfer is requested again
     * @param requestBehaviour job transfer request to invoke
     */
    public RetryJobTransferRequestInCloudNetwork(ServerAgent agent,
                                                 long timeout,
                                                 PowerShortageJob jobToTransfer,
                                                 Behaviour requestBehaviour) {
        super(agent, timeout);
        this.myServerAgent = agent;
        this.jobToTransfer = jobToTransfer;
        this.requestBehaviour = requestBehaviour;
        this.guid = myServerAgent.getLocalName();
    }

    @Override
    protected void onWake() {
        if (nonNull(myServerAgent.manage().getJobByIdAndStartDate(jobToTransfer.getJobInstanceId())) &&
                myServerAgent.getServerJobs().get(myServerAgent.manage().getJobByIdAndStartDate(jobToTransfer.getJobInstanceId())).equals(JobStatusEnum.ON_HOLD)) {
            logger.info("[{}] Retrying to transfer the job {}", guid, jobToTransfer.getJobInstanceId().getJobId());
            myServerAgent.addBehaviour(requestBehaviour);
        } else {
            logger.info("[{}] Job {} is no longer on hold", guid, jobToTransfer.getJobInstanceId().getJobId());
        }
    }
}
