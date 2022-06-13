package agents.cloudnetwork.behaviour;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

import agents.cloudnetwork.CloudNetworkAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import messages.domain.SendJobConfirmationMessage;
import messages.domain.SendJobOfferResponseMessage;
import domain.job.Job;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for sending proposal with job execution offer to the client
 */
public class ProposeJobOffer extends ProposeInitiator {

    private static final Logger logger = LoggerFactory.getLogger(ProposeJobOffer.class);

    private final ACLMessage replyMessage;
    private final CloudNetworkAgent myCloudNetworkAgent;
    private final String guid;

    /**
     * Behaviour constructor.
     *
     * @param agent        agent which is executing the behaviour
     * @param msg          proposal message with job execution price that will be sent to the client
     * @param replyMessage reply message sent to server with ACCEPT/REJECT proposal
     */
    public ProposeJobOffer(final Agent agent, final ACLMessage msg, final ACLMessage replyMessage) {
        super(agent, msg);
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
        this.guid = agent.getName();
        this.replyMessage = replyMessage;
    }

    /**
     * Method handles accept proposal message retrieved from the Client Agent. It sends accept proposal to the
     * chosen for job execution Server Agent and updates the network state.
     *
     * @param accept_proposal received accept proposal message
     */
    @Override
    protected void handleAcceptProposal(final ACLMessage accept_proposal) {
        try {
            logger.debug("[{}] Sending ACCEPT_PROPOSAL to Server Agent", guid);
            final Job job = getMapper().readValue(accept_proposal.getContent(), Job.class);

            updateNetworkInformation(job);
            myAgent.send(SendJobConfirmationMessage.create(job, accept_proposal.createReply()).getMessage());
            myAgent.send(SendJobOfferResponseMessage.create(job, ACCEPT_PROPOSAL, replyMessage).getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method handles reject proposal message retrieved from the Client Agent. It sends reject proposal to the
     * Server Agent previously chosen for the job execution.
     *
     * @param reject_proposal received reject proposal message
     */
    @Override
    protected void handleRejectProposal(final ACLMessage reject_proposal) {
        try {
            logger.debug("[{}] Client {} rejected the job proposal", guid, reject_proposal.getSender().getName());
            final Job job = getMapper().readValue(reject_proposal.getContent(), Job.class);

            myCloudNetworkAgent.getServerForJobMap().remove(job);
            myCloudNetworkAgent.send(SendJobOfferResponseMessage.create(job, REJECT_PROPOSAL, replyMessage).getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void updateNetworkInformation(final Job job) {
        myCloudNetworkAgent.getCurrentJobs().add(job);
        myCloudNetworkAgent.setInUsePower(myCloudNetworkAgent.getInUsePower() + job.getPower());
    }
}
