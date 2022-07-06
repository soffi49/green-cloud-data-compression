package agents.cloudnetwork.behaviour;

import static agents.cloudnetwork.CloudNetworkAgentConstants.SERVER_AGENTS;
import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.CLIENT_JOB_CFP_PROTOCOL;
import static common.constant.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.JobStatusEnum;
import messages.domain.CallForProposalMessageFactory;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Behaviour which is responsible for handling upcoming call for proposals from clients
 */
public class ReceiveJobRequests extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveJobRequests.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(CFP), MatchProtocol(CLIENT_JOB_CFP_PROTOCOL));

    private CloudNetworkAgent myCloudNetworkAgent;

    /**
     * Method runs at the behaviour start. It casts the abstract agent to the agent of type CloudNetworkAgent
     */
    @Override
    public void onStart() {
        super.onStart();
        myCloudNetworkAgent = (CloudNetworkAgent) myAgent;
    }

    /**
     * Method listens for the upcoming job call for proposals from the Client Agents. It announces the job call for proposal
     * to the network by sending call for proposal with job characteristics to owned Server Agents.
     */
    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                final String jobId = job.getJobId();
                if(myCloudNetworkAgent.getJobRequestRetries().containsKey(jobId)) {
                    logger.info("[{}] Sending call for proposal to Server Agents for a job request with jobId {}, {} retry.",
                        myAgent.getName(), jobId, myCloudNetworkAgent.getJobRequestRetries().get(jobId));
                }
                else {
                    myCloudNetworkAgent.getJobRequestRetries().put(jobId, 0);
                    logger.info("[{}] Sending call for proposal to Server Agents for a job request with jobId {}!",
                        myAgent.getName(), jobId);
                }

                final List<AID> serverAgents = (List<AID>) getParent().getDataStore().get(SERVER_AGENTS);
                final ACLMessage cfp = CallForProposalMessageFactory.createCallForProposal(job, serverAgents, CNA_JOB_CFP_PROTOCOL);

                displayMessageArrow(myCloudNetworkAgent, serverAgents);
                myCloudNetworkAgent.getNetworkJobs().put(job, JobStatusEnum.PROCESSING);
                myAgent.addBehaviour(new AnnounceNewJobRequest(myAgent, cfp, message, jobId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
