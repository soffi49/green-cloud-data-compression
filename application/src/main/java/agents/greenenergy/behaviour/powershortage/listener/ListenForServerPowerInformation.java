package agents.greenenergy.behaviour.powershortage.listener;

import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobInstanceIdentifier;
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
 * Behaviour is responsible for listening for the power information coming from the owner server
 */
public class ListenForServerPowerInformation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForServerPowerInformation.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM),
                                                               or(or(MatchProtocol(CANCELLED_TRANSFER_PROTOCOL),
                                                                     MatchProtocol(SERVER_POWER_SHORTAGE_ALERT_PROTOCOL)),
                                                                  or(MatchProtocol(POWER_SHORTAGE_FINISH_ALERT_PROTOCOL),
                                                                     MatchProtocol(SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL))));

    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent agent executing the behaviour
     */
    public ListenForServerPowerInformation(final Agent myAgent) {
        super(myAgent);
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
    }

    /**
     * Method listens for the message coming from the Server passing the information regarding power
     * supply changes (cancelling power job in case of transfer, information about power shortage and power shortage finish)
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            switch (inform.getProtocol()) {
                case CANCELLED_TRANSFER_PROTOCOL -> handleCancelledPowerTransfer(inform);
                case SERVER_POWER_SHORTAGE_ALERT_PROTOCOL -> handleServerPowerShortage(inform);
                case POWER_SHORTAGE_FINISH_ALERT_PROTOCOL -> handleFinishedPowerShortage(inform);
                case SERVER_POWER_SHORTAGE_ON_HOLD_PROTOCOL -> handleServerJobTransferFailure(inform);
            }
        } else {
            block();
        }
    }

    private void handleServerJobTransferFailure(final ACLMessage inform) {
        try {
            final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
            if (Objects.nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                logger.info("[{}] Received information about job {} transfer failure. Putting job on hold",
                            myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
                final PowerJob jobToPutOnHold = myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId());
                myGreenEnergyAgent.getPowerJobs().replace(jobToPutOnHold, JobStatusEnum.ON_HOLD);
                myGreenEnergyAgent.manage().updateGreenSourceGUI();
            } else {
                logger.info("[{}] Job {} to put on hold was not found", myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCancelledPowerTransfer(final ACLMessage inform) {
        try {
            final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
            final PowerJob jobToCancel = myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId().getJobId(), powerShortageJob.getPowerShortageStart());
            if (Objects.nonNull(jobToCancel)) {
                logger.info("[{}] Cancelling the job with id {}", myGreenEnergyAgent.getLocalName(), jobToCancel.getJobId());
                if (myGreenEnergyAgent.getPowerJobs().get(jobToCancel).equals(JobStatusEnum.IN_PROGRESS)) {
                    myGreenEnergyAgent.manage().incrementFinishedJobs(jobToCancel.getJobId());
                }
                myGreenEnergyAgent.getPowerJobs().remove(jobToCancel);
            } else {
                logger.info("[{}] Job {} to cancel was not found", myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleServerPowerShortage(final ACLMessage inform) {
        try {
            final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
            if (Objects.nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                logger.info("[{}] Received information about job {} power shortage in server. Updating green source state",
                            myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
                final PowerJob jobToDivide = myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId());
                myGreenEnergyAgent.manage().divideJobForPowerShortage(jobToDivide, powerShortageJob.getPowerShortageStart());
            } else {
                logger.info("[{}] Job {} to divide due to power shortage was not found", myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleFinishedPowerShortage(final ACLMessage inform) {
        try {
            final JobInstanceIdentifier jobInstanceId = getMapper().readValue(inform.getContent(), JobInstanceIdentifier.class);
            final PowerJob powerJob = myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobInstanceId);
            if (Objects.nonNull(powerJob)) {
                logger.info("[{}] Power shortage in server finished. Changing the status of the power job {}", myGreenEnergyAgent.getLocalName(), jobInstanceId.getJobId());
                final JobStatusEnum newStatus = powerJob.getStartTime().isAfter(getCurrentTime()) ? JobStatusEnum.ACCEPTED : JobStatusEnum.IN_PROGRESS;
                myGreenEnergyAgent.getPowerJobs().replace(powerJob, newStatus);
                myGreenEnergyAgent.manage().updateGreenSourceGUI();
            } else {
                logger.info("[{}] Job {} to supply with green power was not found", myGreenEnergyAgent.getLocalName(), jobInstanceId.getJobId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
