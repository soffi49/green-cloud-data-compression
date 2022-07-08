package agents.cloudnetwork.behaviour;

import static agents.cloudnetwork.CloudNetworkAgentConstants.MAX_POWER_DIFFERENCE;
import static agents.cloudnetwork.CloudNetworkAgentConstants.RETRY_LIMIT;
import static agents.cloudnetwork.CloudNetworkAgentConstants.RETRY_PAUSE_MILLISECONDS;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.domain.JobOfferMessageFactory.makeJobOfferForClient;

import agents.cloudnetwork.CloudNetworkAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.constant.InvalidJobIdConstant;
import domain.ServerData;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;
import messages.domain.ReplyMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour which is responsible for broadcasting client's job to servers and choosing server to execute the job
 */
public class AnnounceNewJobRequest extends ContractNetInitiator {

    private static final Logger logger = LoggerFactory.getLogger(AnnounceNewJobRequest.class);

    private final ACLMessage originalMessage;
    private final ACLMessage replyMessage;
    private final CloudNetworkAgent myCloudNetworkAgent;
    private final String guid;
    private final transient Predicate<ACLMessage> isValidProposal;
    private final String jobId;

    /**
     * Behaviour constructor.
     *
     * @param agent           agent which is executing the behaviour
     * @param cfp             call for proposal message containing job requriements sent to the servers
     * @param originalMessage original message received from the client
     */
    public AnnounceNewJobRequest(final Agent agent, final ACLMessage cfp, final ACLMessage originalMessage, String jobId) {
        super(agent, cfp);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.guid = agent.getName();
        this.originalMessage = originalMessage;
        this.replyMessage = originalMessage.createReply();
        this.jobId = jobId;
        this.isValidProposal = message -> {
            try {
                getMapper().readValue(message.getContent(), ServerData.class);
                return true;
            } catch (JsonProcessingException e) {
                return false;
            }
        };
    }

    /**
     * Method which waits for all Server Agent responses. It is responsible for analyzing the received proposals,
     * choosing the Server Agent for job execution and rejecting the remaining Server Agents.
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
            var retries = myCloudNetworkAgent.getJobRequestRetries().get(jobId);
            if (retries >= RETRY_LIMIT) {
                logger.info("[{}] No Servers available - reached limit of retires - sending refuse message to client", guid);
                myCloudNetworkAgent.getJobRequestRetries().remove(jobId);
                myAgent.send(ReplyMessageFactory.prepareRefuseReply(replyMessage));
            } else {
                myCloudNetworkAgent.getJobRequestRetries().put(jobId, ++retries);
                logger.info("[{}] No Servers available - schedule {} retry to process the message", guid, retries);
                myAgent.addBehaviour(new ScheduleRetry(myCloudNetworkAgent, RETRY_PAUSE_MILLISECONDS, originalMessage,
                    jobId));
            }
        } else {
            List<ACLMessage> validProposals = proposals.stream().filter(isValidProposal).toList();

            if (validProposals.isEmpty()) {
                logger.info("[{}] I didn't understand any proposal from Server Agents", guid);
                rejectJobOffers(myCloudNetworkAgent, InvalidJobIdConstant.INVALID_JOB_ID, null, proposals);
                myAgent.send(ReplyMessageFactory.prepareRefuseReply(replyMessage));
                return;
            }

            ACLMessage chosenServerOffer = chooseServerToExecuteJob(validProposals);
            ServerData chosenServerData;

            try {
                chosenServerData = getMapper().readValue(chosenServerOffer.getContent(), ServerData.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            logger.info("[{}] Chosen Server for the job: {}", guid, chosenServerOffer.getSender().getName());
            final ACLMessage serverReplyMessage = chosenServerOffer.createReply();

            logger.info("[{}] Sending job execution offer to Client", guid);
            myCloudNetworkAgent.getServerForJobMap().put(chosenServerData.getJobId(), chosenServerOffer.getSender());
            myCloudNetworkAgent.addBehaviour(new ProposeJobOffer(myCloudNetworkAgent,
                makeJobOfferForClient(chosenServerData, myCloudNetworkAgent.getCurrentPowerInUse(), replyMessage),
                serverReplyMessage));
            rejectJobOffers(myCloudNetworkAgent, chosenServerData.getJobId(), chosenServerOffer, proposals);
        }
    }

    private ACLMessage chooseServerToExecuteJob(final List<ACLMessage> serverOffers) {
        return serverOffers.stream().min(this::compareServerOffers).orElseThrow();
    }

    private int compareServerOffers(final ACLMessage serverOffer1, final ACLMessage serverOffer2) {
        ServerData server1;
        ServerData server2;
        try {
            server1 = getMapper().readValue(serverOffer1.getContent(), ServerData.class);
            server2 = getMapper().readValue(serverOffer2.getContent(), ServerData.class);
        } catch (JsonProcessingException e) {
            return Integer.MAX_VALUE;
        }
        int powerDifference = server1.getAvailablePower() - server2.getAvailablePower();
        int priceDifference = (int) (server1.getServicePrice() - server2.getServicePrice());
        return MAX_POWER_DIFFERENCE.isValidIntValue(powerDifference) ? priceDifference : powerDifference;
    }
}
