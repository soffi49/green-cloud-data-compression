package agents.greenenergy.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;

import static common.GUIUtils.updateGreenSourceState;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.prepareTransferCancellationRequest;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.PowerShortageJob;
import domain.job.PowerJob;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour is responsible for catching the information confirming that the job transfer was successful
 */
public class ListenForTransferConfirmation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForTransferConfirmation.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL));

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myGreenEnergyAgent agent which is executing the behaviour
     */
    public ListenForTransferConfirmation(final GreenEnergyAgent myGreenEnergyAgent) {
        this.myGreenEnergyAgent = myGreenEnergyAgent;
        this.guid = myGreenEnergyAgent.getName();
    }

    /**
     * Method which listens for the information that the transfer for the given job is established.
     * It then verifies in which state is the job and based on that, handles the transfer information.
     */
    @Override
    public void action() {
        final ACLMessage message = myGreenEnergyAgent.receive(messageTemplate);
        if (nonNull(message)) {
            final PowerShortageJob powerShortageJob = readMessage(message);
            if (Objects.nonNull(powerShortageJob) && nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId().getJobId(), powerShortageJob.getPowerShortageStart()))) {
                final String jobId = powerShortageJob.getJobInstanceId().getJobId();
                logger.info("[{}] Transfer of job with id {} was established successfully", guid, jobId);
                final PowerJob powerJob = myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobId, powerShortageJob.getPowerShortageStart());
                final boolean willJobFinishBeforeTransfer = powerJob.getEndTime().isBefore(powerShortageJob.getPowerShortageStart()) ||
                        powerJob.getEndTime().isEqual(powerShortageJob.getPowerShortageStart());

                if (willJobFinishBeforeTransfer) {
                    logger.info("[{}] Job with id {} will finish before the transfer. Sending cancel transfer information ", guid, jobId);
                    displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getOwnerServer());
                    myGreenEnergyAgent.send(prepareTransferCancellationRequest(powerShortageJob, myGreenEnergyAgent.getOwnerServer()));
                } else {
                    logger.info("[{}] Finishing job with id {} on power shortage", guid, jobId);
                    myGreenEnergyAgent.getPowerJobs().remove(powerJob);
                    updateGreenSourceState(myGreenEnergyAgent);
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
