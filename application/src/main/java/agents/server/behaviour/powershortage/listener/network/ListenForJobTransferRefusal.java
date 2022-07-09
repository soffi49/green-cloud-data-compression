package agents.server.behaviour.powershortage.listener.network;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_TRANSFER_REFUSAL;
import static domain.job.JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;

import agents.server.ServerAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Behaviour is responsible for retrieving the information send by the Cloud Network stating that the job transfer was unsuccessful
 */
public class ListenForJobTransferRefusal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobTransferRefusal.class);
    private final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_TRANSFER_REFUSAL));

    private final ServerAgent myServerAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param myServerAgent agent which is executing the behaviour
     */
    public ListenForJobTransferRefusal(final ServerAgent myServerAgent) {
        this.myServerAgent = myServerAgent;
        this.guid = myServerAgent.getName();
    }

    /**
     * Method which listens for the information from cloud network that the transfer for the given job couldn't been
     * established. Due to that, it updates the states of the job.
     */
    @Override
    public void action() {
        final ACLMessage message = myServerAgent.receive(messageTemplate);
        if (nonNull(message)) {
            final PowerShortageJob powerShortageJob = readMessage(message);
            if (Objects.nonNull(powerShortageJob) && nonNull(myServerAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                final String transferredJobId = powerShortageJob.getJobInstanceId().getJobId();
                logger.info("[{}] Transfer of job with id {} was unsuccessful! Supplying the job with back up power", guid, transferredJobId);
                final Job jobOnBackUp = myServerAgent.manage().getJobByIdAndStartDate(transferredJobId, powerShortageJob.getJobInstanceId().getStartTime());
                final String jobId = jobOnBackUp.getJobId();

                logger.info("[{}] Informing green source to switch the job {} on hold", guid, jobId);
                final AID receiver = myServerAgent.getGreenSourceForJobMap().get(jobId);
                myServerAgent.getServerJobs().replace(jobOnBackUp, IN_PROGRESS_BACKUP_ENERGY);
                myServerAgent.manage().updateServerGUI();
                displayMessageArrow(myServerAgent, receiver);
                myServerAgent.send(prepareJobPowerShortageInformation(powerShortageJob,receiver, POWER_SHORTAGE_TRANSFER_REFUSAL));
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
