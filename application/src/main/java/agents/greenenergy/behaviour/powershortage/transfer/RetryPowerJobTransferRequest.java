package agents.greenenergy.behaviour.powershortage.transfer;

import static java.util.Objects.nonNull;

import agents.greenenergy.GreenEnergyAgent;
import agents.server.ServerAgent;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour is responsible for retrying to transfer the job which was put on hold
 */
public class RetryPowerJobTransferRequest extends WakerBehaviour {


    private static final Logger logger = LoggerFactory.getLogger(RetryPowerJobTransferRequest.class);

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final JobInstanceIdentifier jobToTransfer;
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
    public RetryPowerJobTransferRequest(GreenEnergyAgent agent,
                                        long timeout,
                                        JobInstanceIdentifier jobToTransfer,
                                        Behaviour requestBehaviour) {
        super(agent, timeout);
        this.myGreenEnergyAgent = agent;
        this.jobToTransfer = jobToTransfer;
        this.requestBehaviour = requestBehaviour;
        this.guid = myGreenEnergyAgent.getLocalName();
    }

    @Override
    protected void onWake() {
        if (nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobToTransfer)) &&
                myGreenEnergyAgent.getPowerJobs().get(myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobToTransfer)).equals(JobStatusEnum.ON_HOLD)) {
            logger.info("[{}] Retrying to transfer the power job {}", guid, jobToTransfer.getJobId());
            myGreenEnergyAgent.addBehaviour(requestBehaviour);
        } else {
            logger.info("[{}] Power job {} is no longer on hold", guid, jobToTransfer.getJobId());
        }
    }
}
