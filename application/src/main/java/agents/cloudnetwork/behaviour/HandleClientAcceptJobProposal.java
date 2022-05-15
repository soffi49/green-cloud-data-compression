package agents.cloudnetwork.behaviour;

import agents.client.message.SendJobMessage;
import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.CFP;
import static mapper.JsonMapper.getMapper;

/**
 * Cyclic behaviour for Cloud Network Agent. It purpose is to handle the accept job proposal response from Client
 */
public class HandleClientAcceptJobProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleClientAcceptJobProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACCEPT_PROPOSAL);

    private HandleClientAcceptJobProposal(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleClientAcceptJobProposal createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleClientAcceptJobProposal(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            ((CloudNetworkAgent) myAgent).setJobsCount(((CloudNetworkAgent) myAgent).getJobsCount() + 1);
            try {
                logger.info("[{}] Sending call for proposal to server agents", myAgent);

                final Job job = getMapper().readValue(message.getContent(), Job.class);
                ((CloudNetworkAgent) myAgent).getCurrentJobs().add(job);
                ((CloudNetworkAgent) myAgent).setInUsePower(((CloudNetworkAgent) myAgent).getInUsePower() + job.getPower());
                myAgent.send(SendJobMessage.create(job, ((CloudNetworkAgent) myAgent).getServerAgentList(), CFP).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
