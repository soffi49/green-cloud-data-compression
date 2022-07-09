package agents.greenenergy.behaviour.powershortage.listener;

import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_TRANSFER_REFUSAL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour listens for the information that the power transfer couldn't be established and that the given
 * job should be put on hold
 */
public class ListenForPowerTransferRefusal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForPowerTransferRefusal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_TRANSFER_REFUSAL));

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myGreenEnergyAgent agent which is executing the behaviour
     */
    public ListenForPowerTransferRefusal(final GreenEnergyAgent myGreenEnergyAgent) {
        this.myGreenEnergyAgent = myGreenEnergyAgent;
        this.guid = myGreenEnergyAgent.getName();
    }

    /**
     * Method which listens for the information that the transfer for the given job was not established. It updates
     * the status of the given power job to on hold
     */
    @Override
    public void action() {
        final ACLMessage message = myGreenEnergyAgent.receive(messageTemplate);
        if (nonNull(message)) {
            final PowerShortageJob powerShortageJob = readMessage(message);
            if (Objects.nonNull(powerShortageJob) && nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                final String jobId = powerShortageJob.getJobInstanceId().getJobId();
                logger.info("[{}] Transfer of job with id {} was unsuccessful!", guid, jobId);
                final PowerJob powerJobBackUp = myGreenEnergyAgent.manage().getJobByIdAndStartDate(jobId, powerShortageJob.getJobInstanceId().getStartTime());

                logger.info("[{}] Putting the job with id {} on hold", guid, jobId);
                myGreenEnergyAgent.getPowerJobs().replace(powerJobBackUp, JobStatusEnum.ON_HOLD);
                myGreenEnergyAgent.manage().updateGreenSourceGUI();
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
