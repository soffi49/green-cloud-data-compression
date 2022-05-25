package agents.cloudnetwork.behaviour;

import agents.client.message.SendJobMessage;
import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static common.GroupConstants.SA_SERVICE_TYPE;
import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.CFP;
import static mapper.JsonMapper.getMapper;
import static yellowpages.YellowPagesService.search;

/**
 * Cyclic behaviour for Cloud Network Agent. It purpose is to handle the accept job proposal response from Client
 */
public class HandleClientAcceptJobProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleClientAcceptJobProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACCEPT_PROPOSAL);

    private CloudNetworkAgent myCloudAgent;

    private HandleClientAcceptJobProposal(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleClientAcceptJobProposal createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleClientAcceptJobProposal(cloudNetworkAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        myCloudAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            myCloudAgent.setJobsCount(myCloudAgent.getJobsCount() + 1);
            try {
                logger.info("[{}] Sending accept proposal to server agent", myAgent);
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                final AID serverForJob = myCloudAgent.getServerForJobMap().get(job);
                myCloudAgent.getCurrentJobs().add(job);
                myCloudAgent.setInUsePower(myCloudAgent.getInUsePower() + job.getPower());
                myAgent.send(SendJobMessage.create(job, List.of(serverForJob), ACCEPT_PROPOSAL).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
