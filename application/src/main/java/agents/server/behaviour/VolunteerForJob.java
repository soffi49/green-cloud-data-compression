package agents.server.behaviour;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import common.message.SendJobOfferResponseMessage;
import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviours responsible for sending volunteering offer to CNA
 */
public class VolunteerForJob extends ProposeInitiator {

    private static final Logger logger = LoggerFactory.getLogger(VolunteerForJob.class);

    private ServerAgent myServerAgent;

    public VolunteerForJob(final Agent a, final ACLMessage msg, final DataStore dataStore) {
        super(a, msg, dataStore);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.myServerAgent = (ServerAgent) myAgent;
    }

    @Override
    protected void handleAcceptProposal(final ACLMessage accept_proposal) {
        try {
            logger.info("[{}] Sending ACCEPT_PROPOSAL to Green Source Agent", myAgent);

            final Job job = getMapper().readValue(accept_proposal.getContent(), Job.class);
            final AID greenSourceForJob = myServerAgent.getGreenSourceForJobMap().get(job);
            final ACLMessage acceptanceMessage = (ACLMessage) getDataStore().get(greenSourceForJob);

            myAgent.addBehaviour(new StartJobExecution());
            myAgent.send(SendJobOfferResponseMessage.create(job, ACCEPT_PROPOSAL, acceptanceMessage.createReply()).getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleRejectProposal(final ACLMessage reject_proposal) {
        try {
            logger.info("[{}] Cloud Network {} rejected the job volunteering offer", myAgent, reject_proposal.getSender().getLocalName());
            final Job job = getMapper().readValue(reject_proposal.getContent(), Job.class);
            final AID serverToReject = myServerAgent.getGreenSourceForJobMap().get(job);
            final ACLMessage rejectionMessage = (ACLMessage) getDataStore().get(serverToReject);

            myServerAgent.getGreenSourceForJobMap().remove(job);
            myServerAgent.send(SendJobOfferResponseMessage.create(job, REJECT_PROPOSAL, rejectionMessage.createReply()).getMessage());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
