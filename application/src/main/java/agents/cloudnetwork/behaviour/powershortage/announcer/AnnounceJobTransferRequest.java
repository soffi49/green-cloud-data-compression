package agents.cloudnetwork.behaviour.powershortage.announcer;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.BACK_UP_POWER_JOB_PROTOCOL;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.MessagingUtils.retrieveValidMessages;
import static messages.domain.JobStatusMessageFactory.prepareJobStatusMessageForClient;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.behaviour.powershortage.handler.TransferJobToServer;
import agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerTransferCancellation;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ServerData;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import messages.domain.ReplyMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Vector;

/**
 * Behaviour which is responsible for sending call for proposal to remaining server agents asking for job transfer
 */
public class AnnounceJobTransferRequest extends ContractNetInitiator {
    private static final Logger logger = LoggerFactory.getLogger(AnnounceJobTransferRequest.class);

    private final CloudNetworkAgent myCloudNetworkAgent;
    private final String guid;
    private final ACLMessage serverRequest;
    private final PowerShortageJob jobTransfer;
    private final AID jobClient;

    /**
     * Behaviour constructor.
     *
     * @param agent         agent which is executing the behaviour
     * @param cfp           call for proposal message containing job requriements sent to the servers
     * @param serverRequest transfer request message coming from the server
     * @param jobTransfer   job for which the transfer is being performed
     * @param jobClient     client which should be informed about job status updates
     */
    public AnnounceJobTransferRequest(final Agent agent,
                                      final ACLMessage cfp,
                                      final ACLMessage serverRequest,
                                      final PowerShortageJob jobTransfer,
                                      final String jobClient) {
        super(agent, cfp);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.guid = agent.getName();
        this.jobTransfer = jobTransfer;
        this.serverRequest = serverRequest;
        this.jobClient = new AID(jobClient, AID.ISGUID);
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
            myCloudNetworkAgent.send(prepareReply(serverRequest.createReply(), jobTransfer.getJobInstanceId(), ACLMessage.FAILURE));
        } else if (proposals.isEmpty()) {
            logger.info("[{}] No Servers available - sending message to client and server that the job must be executed from backup power", guid);
            displayMessageArrow(myCloudNetworkAgent, jobClient);
            myCloudNetworkAgent.send(prepareJobStatusMessageForClient(jobClient.getName(), BACK_UP_POWER_JOB_PROTOCOL));
            myCloudNetworkAgent.send(prepareReply(serverRequest.createReply(), jobTransfer.getJobInstanceId(), ACLMessage.FAILURE));
        } else {
            final List<ACLMessage> validProposals = retrieveValidMessages(proposals, ServerData.class);
            if (!validProposals.isEmpty()) {
                final ACLMessage chosenServerOffer = chooseServerToExecuteJob(validProposals);
                final ServerData chosenServerData = readMessage(chosenServerOffer);
                logger.info("[{}] Chosen Server for the job {} transfer: {}", guid, chosenServerData.getJobId(), chosenServerOffer.getSender().getName());

                displayMessageArrow(myCloudNetworkAgent, chosenServerOffer.getSender());
                displayMessageArrow(myCloudNetworkAgent, serverRequest.getSender());

                myAgent.send(ReplyMessageFactory.prepareAcceptReplyWithProtocol(chosenServerOffer.createReply(), jobTransfer.getJobInstanceId(), POWER_SHORTAGE_POWER_TRANSFER_PROTOCOL));
                myCloudNetworkAgent.send(prepareReply(serverRequest.createReply(), jobTransfer.getJobInstanceId(), ACLMessage.INFORM));
                preparePowerShortageHandling(chosenServerOffer.getSender());
                rejectJobOffers(myCloudNetworkAgent, jobTransfer.getJobInstanceId(), chosenServerOffer, proposals);
            } else {
                handleInvalidResponses(proposals);
            }
        }
    }

    private void preparePowerShortageHandling(final AID chosenServer) {
        final ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(TransferJobToServer.createFor(myCloudNetworkAgent, jobTransfer, chosenServer));
        parallelBehaviour.addSubBehaviour(new ListenForServerTransferCancellation(myAgent, chosenServer, serverRequest.getSender()));
        myCloudNetworkAgent.addBehaviour(parallelBehaviour);
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
        rejectJobOffers(myCloudNetworkAgent, jobTransfer.getJobInstanceId(), null, proposals);
        myCloudNetworkAgent.send(prepareReply(serverRequest.createReply(), jobTransfer.getJobInstanceId(), ACLMessage.FAILURE));
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
