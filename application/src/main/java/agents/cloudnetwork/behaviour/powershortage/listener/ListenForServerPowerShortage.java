package agents.cloudnetwork.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_ALERT_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.preparePowerShortageMessageForClient;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.behaviour.powershortage.announcer.AnnounceJobTransferRequest;
import domain.job.ImmutableJob;
import domain.job.Job;
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
 * Behaviour is responsible for receiving the information that the server has power shortage and is unable to execute the given job
 */
public class ListenForServerPowerShortage extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForServerPowerShortage.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_ALERT_PROTOCOL));

    private CloudNetworkAgent myCloudNetworkAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Cloud Network Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    /**
     * Method listens for the messages coming from the Server informing that it has some power shortage at the given time.
     * It handles the information by announcing the information in network and looking for another server which may execute the job
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageTransfer powerShortageTransfer = getMapper().readValue(inform.getContent(), PowerShortageTransfer.class);
                final List<AID> remainingServers = getRemainingServers(inform.getSender());
                powerShortageTransfer.getJobList().stream()
                        .map(job -> myCloudNetworkAgent.manage().getJobById(job.getJobId()))
                        .forEach(job -> {
                            if (Objects.nonNull(job)) {
                                if (!remainingServers.isEmpty()) {
                                    logger.info("[{}] Sending call for proposal to Server Agents to transfer job with id {}", myAgent.getName(), job.getJobId());
                                    final ACLMessage cfp = prepareServerCFP(job, powerShortageTransfer.getStartTime(), remainingServers);
                                    displayMessageArrow(myCloudNetworkAgent, remainingServers);
                                    myAgent.addBehaviour(new AnnounceJobTransferRequest(myAgent, cfp, powerShortageTransfer.getStartTime(), inform.getSender(), job));
                                } else {
                                    logger.info("[{}] No servers available. Passing the information to client", myAgent.getName());
                                    displayMessageArrow(myCloudNetworkAgent, new AID(job.getClientIdentifier(), AID.ISGUID));
                                    myCloudNetworkAgent.send(preparePowerShortageMessageForClient(job.getClientIdentifier()));
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private ACLMessage prepareServerCFP(final Job job, final OffsetDateTime startPowerShortageTime, final List<AID> remainingServers) {
        final OffsetDateTime startTime = job.getStartTime().isAfter(startPowerShortageTime)? job.getStartTime() : startPowerShortageTime;
        final Job newJob = ImmutableJob.builder()
                .power(job.getPower())
                .startTime(startTime)
                .endTime(job.getEndTime())
                .jobId(job.getJobId())
                .clientIdentifier(job.getClientIdentifier())
                .build();
        return CallForProposalMessageFactory.createCallForProposal(newJob, remainingServers, CNA_JOB_CFP_PROTOCOL);
    }

    private List<AID> getRemainingServers(final AID serverSender) {
        return myCloudNetworkAgent.getOwnedServers().stream().filter(server -> !server.equals(serverSender)).toList();
    }
}
