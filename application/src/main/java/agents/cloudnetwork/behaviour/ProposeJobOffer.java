package agents.cloudnetwork.behaviour;

import common.message.SendJobOfferResponseMessage;
import agents.cloudnetwork.CloudNetworkAgent;
import agents.cloudnetwork.message.SendJobConfirmationMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

/**
 * Behaviour responsible for sending proposal with job execution offer to the client
 */
public class ProposeJobOffer extends ProposeInitiator {

    private static final Logger logger = LoggerFactory.getLogger(ProposeJobOffer.class);

    private CloudNetworkAgent myCloudNetworkAgent;

    public ProposeJobOffer(final Agent a, final ACLMessage msg, final DataStore dataStore) {
        super(a, msg, dataStore);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    protected void handleAcceptProposal(final ACLMessage accept_proposal) {
        try {
            logger.info("[{}] Sending ACCEPT_PROPOSAL to Server Agent", myAgent.getName());

            final Job job = getMapper().readValue(accept_proposal.getContent(), Job.class);
            final AID serverForJob = myCloudNetworkAgent.getServerForJobMap().get(job);
            final ACLMessage acceptanceMessage = (ACLMessage) getDataStore().get(serverForJob);

            updateNetworkInformation(job);
            myAgent.send(SendJobConfirmationMessage.create(job, accept_proposal.createReply()).getMessage());
            myAgent.send(SendJobOfferResponseMessage.create(job, ACCEPT_PROPOSAL, acceptanceMessage).getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleRejectProposal(final ACLMessage reject_proposal) {
        try {
            logger.info("[{}] Client {} rejected the job proposal", myAgent.getName(), reject_proposal.getSender().getLocalName());
            final Job job = getMapper().readValue(reject_proposal.getContent(), Job.class);
            final AID serverToReject = myCloudNetworkAgent.getServerForJobMap().get(job);
            final ACLMessage rejectionMessage = (ACLMessage) getDataStore().get(serverToReject);

            myCloudNetworkAgent.getServerForJobMap().remove(job);
            myCloudNetworkAgent.send(SendJobOfferResponseMessage.create(job, REJECT_PROPOSAL, rejectionMessage).getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    private void updateNetworkInformation(final Job job) {
        myCloudNetworkAgent.getCurrentJobs().add(job);
        myCloudNetworkAgent.setInUsePower(myCloudNetworkAgent.getInUsePower() + job.getPower());
        myCloudNetworkAgent.setJobsCount(myCloudNetworkAgent.getJobsCount() + 1);
    }
}
