package agents.greenenergy.behaviour.powershortage.listener;

import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour is responsible for listening for the power transfer cancellation request coming from the server agent
 */
public class ListenForPowerTransferCancellation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForPowerTransferCancellation.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(REQUEST), MatchProtocol(CANCELLED_TRANSFER_PROTOCOL));

    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent agent executing the behaviour
     */
    public ListenForPowerTransferCancellation(final Agent myAgent) {
        super(myAgent);
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
    }

    /**
     * Method listens for the message coming from the Server requesting the transfer cancellation. It looks for the job to be cancelled and
     * cancels its execution
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
                final PowerJob jobToCancel = myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId().getJobId(), powerShortageJob.getPowerShortageStart());
                if (Objects.nonNull(jobToCancel)) {
                    logger.info("[{}] Cancelling the job with id {}", myGreenEnergyAgent.getLocalName(), jobToCancel.getJobId());
                    if(myGreenEnergyAgent.getPowerJobs().get(jobToCancel).equals(JobStatusEnum.IN_PROGRESS)) {
                        myGreenEnergyAgent.manage().incrementFinishedJobs(jobToCancel.getJobId());
                    }
                    myGreenEnergyAgent.getPowerJobs().remove(jobToCancel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
