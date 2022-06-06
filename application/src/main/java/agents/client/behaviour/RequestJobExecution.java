package agents.client.behaviour;

import static agents.client.ClientAgentConstants.CLOUD_NETWORK_AGENTS;
import static common.MessagingUtils.rejectJobOffers;
import static common.constant.MessageProtocolConstants.CLIENT_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

import agents.client.ClientAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.message.SendJobCallForProposalMessage;
import common.message.SendJobOfferResponseMessage;
import domain.job.Job;
import domain.job.PricedJob;
import exception.IncorrectCloudNetworkOfferException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Behaviour responsible for sending and handling job's call for proposal
 */
public class RequestJobExecution extends ContractNetInitiator {

    private static final Logger logger = LoggerFactory.getLogger(RequestJobExecution.class);

    private final Job job;
    private ClientAgent myClientAgent;

    public RequestJobExecution(final Agent a, final ACLMessage cfp, final Job job) {
        super(a, cfp);
        this.job = job;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.myClientAgent = (ClientAgent) myAgent;
    }

    @Override
    protected Vector prepareCfps(final ACLMessage callForProposal) {
        logger.info("[{}] Sending call for proposal to Cloud Network Agents", myAgent.getName());
        final Vector<ACLMessage> vector = new Vector<>();
        final List<AID> cloudNetworks = (List<AID>) getParent().getDataStore().get(CLOUD_NETWORK_AGENTS);

        vector.add(SendJobCallForProposalMessage.create(job, cloudNetworks, CLIENT_JOB_CFP_PROTOCOL).getMessage());
        return vector;
    }

    @Override
    protected void handleAllResponses(final Vector responses, final Vector acceptances) {
        final List<ACLMessage> proposals = ((Vector<ACLMessage>) responses).stream()
                .filter(response -> response.getPerformative() == ACLMessage.PROPOSE)
                .toList();

        if (responses.isEmpty()) {
            logger.info("[{}] No responses were retrieved", myAgent.getName());
            myAgent.doDelete();
        } else if (proposals.isEmpty()) {
            logger.info("[{}] All Cloud Network Agents refused to the call for proposal", myAgent.getName());
            myAgent.doDelete();
        } else {
            final ACLMessage chosenOffer = chooseCNAToExecuteJob(proposals);
            logger.info("[{}] Sending ACCEPT_PROPOSAL to {}", myAgent.getName(), chosenOffer.getSender().getName());
            myClientAgent.setChosenCloudNetworkAgent(chosenOffer.getSender());
            acceptances.add(SendJobOfferResponseMessage.create(job, ACCEPT_PROPOSAL, chosenOffer.createReply()).getMessage());
            rejectJobOffers(myClientAgent, job, chosenOffer, proposals);
        }
    }

    @Override
    protected void handleInform(final ACLMessage inform) {
        logger.info("[{}] The execution of my job started!", myAgent);
    }

    private ACLMessage chooseCNAToExecuteJob(final List<ACLMessage> receivedOffers) {
        final Comparator<ACLMessage> compareCNA = Comparator.comparingDouble(offer -> {
            try {
                return getMapper().readValue(offer.getContent(), PricedJob.class).getPriceForJob();
            } catch (JsonProcessingException e) {
                throw new IncorrectCloudNetworkOfferException();
            }
        });
        return receivedOffers.stream().min(compareCNA).orElseThrow();
    }
}
