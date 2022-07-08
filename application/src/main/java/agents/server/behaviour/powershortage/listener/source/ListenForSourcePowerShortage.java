package agents.server.behaviour.powershortage.listener.source;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.preparePowerShortageInformation;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.announcer.AnnouncePowerRequestTransfer;
import agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.job.PowerJob;
import domain.job.PowerShortageTransfer;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import messages.domain.CallForProposalMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Behaviour is responsible for catching the information that the specific green source will have the power shortage at the given time
 */
public class ListenForSourcePowerShortage extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForSourcePowerShortage.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_ALERT_PROTOCOL));

    private ServerAgent myServerAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    /**
     * Method listens for the messages coming from the Green Source informing about the detected power shortage.
     * Then, it sends the call for proposal for all jobs which need to be transferred to all other green sources. If there are no
     * available green sources, it passes the transfer request to the parent cloud network
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageTransfer powerShortageTransfer = getMapper().readValue(inform.getContent(), PowerShortageTransfer.class);
                final List<AID> remainingGreenSources = getRemainingGreenSources(inform.getSender());
                if (!remainingGreenSources.isEmpty() && !powerShortageTransfer.getJobList().isEmpty()) {
                    powerShortageTransfer.getJobList().forEach(job -> {
                        logger.info("[{}] Sending call for proposal to Green Source Agents to transfer job with id {}", myAgent.getName(), job.getJobId());
                        final OffsetDateTime startTime = job.getStartTime().isAfter(powerShortageTransfer.getStartTime())? job.getStartTime() : powerShortageTransfer.getStartTime();
                        final PowerJob powerJob = JobMapper.mapPowerJobToPowerJob(job, startTime);
                        final ACLMessage cfp = CallForProposalMessageFactory.createCallForProposal(powerJob, remainingGreenSources, SERVER_JOB_CFP_PROTOCOL);
                        displayMessageArrow(myServerAgent, remainingGreenSources);
                        myAgent.addBehaviour(new AnnouncePowerRequestTransfer(myAgent, cfp, powerJob, powerShortageTransfer));
                    });
                } else if (remainingGreenSources.isEmpty()) {
                    logger.info("[{}] No green sources available. Passing power shortage information to cloud network", myAgent.getName());
                    displayMessageArrow(myServerAgent, myServerAgent.getOwnerCloudNetworkAgent());
                    myServerAgent.send(preparePowerShortageInformation(powerShortageTransfer, myServerAgent.getOwnerCloudNetworkAgent()));
                }
                if (!powerShortageTransfer.getJobList().isEmpty()) {
                    final List<Job> jobList = powerShortageTransfer.getJobList().stream()
                            .map(job -> myServerAgent.manage().getJobByIdAndStartDate(job.getJobId(), job.getStartTime()))
                            .toList();
                    logger.info("[{}] Scheduling power shortage handling", myAgent.getName());
                    createNewJobInstances(jobList, powerShortageTransfer.getStartTime());
                    myServerAgent.addBehaviour(HandleServerPowerShortage.createFor(jobList, powerShortageTransfer.getStartTime(), myServerAgent, null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private void createNewJobInstances(final List<Job> jobList, final OffsetDateTime shortageTime) {
        jobList.forEach(job -> myServerAgent.manage().divideJobForPowerShortage(job, shortageTime));
    }

    private List<AID> getRemainingGreenSources(final AID greenSourceSender) {
        return myServerAgent.getOwnedGreenSources().stream().filter(greenSource -> !greenSource.equals(greenSourceSender)).toList();
    }
}
