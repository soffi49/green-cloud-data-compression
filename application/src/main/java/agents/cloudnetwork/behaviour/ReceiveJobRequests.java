package agents.cloudnetwork.behaviour;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.CLIENT_JOB_CFP_PROTOCOL;
import static common.constant.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import messages.domain.CallForProposalMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                logger.info("[{}] Sending call for proposal to Server Agents", myAgent.getName());

                final Job job = getMapper().readValue(message.getContent(), Job.class);
                final ACLMessage cfp = CallForProposalMessageFactory.createCallForProposal(job, myCloudNetworkAgent.getOwnedServers(), CNA_JOB_CFP_PROTOCOL);

                displayMessageArrow(myCloudNetworkAgent, myCloudNetworkAgent.getOwnedServers());
                myCloudNetworkAgent.getNetworkJobs().put(job, JobStatusEnum.PROCESSING);
                myAgent.addBehaviour(new AnnounceNewJobRequest(myAgent, cfp, message.createReply()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
