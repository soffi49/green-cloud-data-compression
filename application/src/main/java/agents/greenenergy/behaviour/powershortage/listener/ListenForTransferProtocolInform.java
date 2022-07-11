package agents.greenenergy.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.or;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour is responsible for catching the information confirming that the job transfer from either
 * source or server was successful
 */
public class ListenForTransferProtocolInform extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForTransferProtocolInform.class);
    private static final MessageTemplate messageTemplate =
        MessageTemplate.and(
            MatchPerformative(INFORM), or(
                MatchProtocol(POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL),
                MatchProtocol(POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL)
            ));

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myGreenEnergyAgent agent which is executing the behaviour
     */
    public ListenForTransferProtocolInform(final GreenEnergyAgent myGreenEnergyAgent) {
        this.myGreenEnergyAgent = myGreenEnergyAgent;
        this.guid = myGreenEnergyAgent.getName();
    }

    /**
     * Method which listens for the information that the transfer for the given job is established. It then verifies in
     * which state is the job and based on that, handles the transfer information.
     */
    @Override
    public void action() {
        final ACLMessage message = myGreenEnergyAgent.receive(messageTemplate);
        if (nonNull(message)) {
            if(message.getProtocol().equals(POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL)) {
                final PowerShortageJob powerShortageJob = readMessage(message);
                if (Objects.nonNull(powerShortageJob)
                    && nonNull(
                    myGreenEnergyAgent
                        .manage()
                        .getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                    final String jobId = powerShortageJob.getJobInstanceId().getJobId();
                    logger.info("[{}] Transfer of job with id {} was established successfully", guid, jobId);
                    final PowerJob powerJobBackUp =
                        myGreenEnergyAgent
                            .manage()
                            .getJobByIdAndStartDate(jobId, powerShortageJob.getJobInstanceId().getStartTime());
                    final boolean willJobFinishBeforeTransfer =
                        powerJobBackUp.getEndTime().isBefore(powerShortageJob.getPowerShortageStart())
                            || powerJobBackUp.getEndTime().isEqual(powerShortageJob.getPowerShortageStart());

                    if (willJobFinishBeforeTransfer) {
                        logger.info(
                            "[{}] Job with id {} will finish before the transfer. Sending cancel transfer information ",
                            guid,
                            jobId);
                        displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getOwnerServer());
                        myGreenEnergyAgent.send(
                            prepareTransferCancellationRequest(
                                powerShortageJob, myGreenEnergyAgent.getOwnerServer()));
                    } else {
                        logger.info("[{}] Finishing job with id {} on power shortage", guid, jobId);
                        myGreenEnergyAgent.getPowerJobs().remove(powerJobBackUp);
                        if (powerJobBackUp.getStartTime().isBefore(getCurrentTime())) {
                            myGreenEnergyAgent.manage().incrementFinishedJobs(powerJobBackUp.getJobId());
                        }
                    }
                }
            } else if (message.getProtocol().equals(POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL)) {
                try {
                    final PowerShortageJob powerShortageJob = getMapper().readValue(message.getContent(), PowerShortageJob.class);
                    if(Objects.nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                        logger.info("[{}] Received information about job {} power shortage in server. Updating green source state",
                            myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
                        final PowerJob jobToDivide = myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId());
                        myGreenEnergyAgent.manage().divideJobForPowerShortage(jobToDivide, powerShortageJob.getPowerShortageStart());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            block();
        }
    }

    private PowerShortageJob readMessage(final ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), PowerShortageJob.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
