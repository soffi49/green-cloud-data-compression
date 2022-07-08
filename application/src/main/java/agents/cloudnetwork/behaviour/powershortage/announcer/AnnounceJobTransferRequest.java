package agents.cloudnetwork.behaviour.powershortage.announcer;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.MessagingUtils.retrieveValidMessages;
import static messages.domain.JobStatusMessageFactory.preparePowerShortageMessageForClient;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.behaviour.powershortage.handler.TransferJobToServer;
import agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerTransferCancellation;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.constant.InvalidJobIdConstant;
import common.mapper.JobMapper;
import domain.ServerData;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Vector;
import messages.domain.ReplyMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour which is responsible for sending call for proposal to remaining server agents asking for job transfer
 */
public class AnnounceJobTransferRequest extends ContractNetInitiator {
    private static final Logger logger = LoggerFactory.getLogger(AnnounceJobTransferRequest.class);

    private final CloudNetworkAgent myCloudNetworkAgent;
    private final String guid;
    private final OffsetDateTime powerShortageTime;
    private final AID affectedServer;
    private final Job job;

    /**
     * Behaviour constructor.
     *
     * @param agent              agent which is executing the behaviour
     * @param cfp                call for proposal message containing job requriements sent to the servers
     * @param powerShortageStart time when the power shortage will begin
     * @param affectedServer     server which suffers from the power shortage
     * @param job                job that is being transferred
     */
    public AnnounceJobTransferRequest(final Agent agent,
                                      final ACLMessage cfp,
                                      final OffsetDateTime powerShortageStart,
                                      final AID affectedServer,
                                      final Job job) {
        super(agent, cfp);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.guid = agent.getName();
        this.powerShortageTime = powerShortageStart;
        this.affectedServer = affectedServer;
        this.job = job;
    }

    /**
     * Method which waits for all Server Agent responses. It then chooses one server to which the job will be transferred.
     * If no servers are available, it sends the information to server with power shortage that it should use the backup power.
     *
     * @param responses   retrieved responses from Server Agents
     * @param acceptances vector containing accept proposal message sent back to the chosen server (not used)
     */
    @Override
    protected void handleAllResponses(final Vector responses, final Vector acceptances) {
        final List<ACLMessage> proposals = retrieveProposals(responses);

        if (responses.isEmpty()) {
            logger.info("[{}] No responses were retrieved", guid);
        } else if (proposals.isEmpty()) {
            logger.info("[{}] No Servers available - sending message to client that the job must be executed from backup power", guid);
            displayMessageArrow(myCloudNetworkAgent, new AID(job.getClientIdentifier(), AID.ISGUID));
            myCloudNetworkAgent.send(preparePowerShortageMessageForClient(job.getClientIdentifier()));
        } else {
            final List<ACLMessage> validProposals = retrieveValidMessages(proposals, ServerData.class);
            if (!validProposals.isEmpty()) {
                final ACLMessage chosenServerOffer = chooseServerToExecuteJob(validProposals);
                final ServerData chosenServerData = readMessage(chosenServerOffer);
                logger.info("[{}] Chosen Server for the job {} transfer: {}", guid, chosenServerData.getJobId(), chosenServerOffer.getSender().getName());
                final JobInstanceIdentifier jobInstanceId = JobMapper.mapToJobInstanceId(job);

                displayMessageArrow(myCloudNetworkAgent, chosenServerOffer.getSender());
                displayMessageArrow(myCloudNetworkAgent, affectedServer);

                myAgent.send(ReplyMessageFactory.prepareAcceptReplyWithProtocol(chosenServerOffer.createReply(), jobInstanceId, POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL));
                myAgent.send(prepareJobPowerShortageInformation(jobInstanceId, powerShortageTime, affectedServer, POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL));
                myAgent.addBehaviour(TransferJobToServer.createFor(myCloudNetworkAgent, job.getJobId(), powerShortageTime, chosenServerOffer.getSender()));
                myAgent.addBehaviour(new ListenForServerTransferCancellation(myAgent, chosenServerOffer.getSender(), affectedServer));
                rejectJobOffers(myCloudNetworkAgent, chosenServerData.getJobId(), chosenServerOffer, proposals);
            } else {
                handleInvalidResponses(proposals);
            }
        }
    }

    private ServerData readMessage(final ACLMessage message) {
        try {
            return getMapper().readValue(message.getContent(), ServerData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private void handleInvalidResponses(final List<ACLMessage> proposals) {
        logger.info("[{}] I didn't understand any proposal from Server Agents", guid);
        rejectJobOffers(myCloudNetworkAgent, InvalidJobIdConstant.INVALID_JOB_ID, null, proposals);
    }

    private ACLMessage chooseServerToExecuteJob(final List<ACLMessage> serverOffers) {
        return serverOffers.stream().min(this::compareServerOffers).orElseThrow();
    }

    private int compareServerOffers(final ACLMessage serverOffer1, final ACLMessage serverOffer2) {
        final ServerData server1;
        final ServerData server2;
        try {
            server1 = getMapper().readValue(serverOffer1.getContent(), ServerData.class);
            server2 = getMapper().readValue(serverOffer2.getContent(), ServerData.class);
            return server1.getAvailablePower() - server2.getAvailablePower();
        } catch (JsonProcessingException e) {
            return Integer.MAX_VALUE;
        }
    }
}
