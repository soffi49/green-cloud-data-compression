package agents.cloudnetwork.behaviour;

import static common.GroupConstants.SA_SERVICE_TYPE;
import static jade.lang.acl.ACLMessage.CFP;
import static mapper.JsonMapper.getMapper;

import static yellowpages.YellowPagesService.search;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cyclic behaviour for Cloud Network Agent. It purpose is to handle the call for proposal from Client
 */
public class HandleClientJobCallForProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleClientJobCallForProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(CFP);

    private HandleClientJobCallForProposal(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleClientJobCallForProposal createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleClientJobCallForProposal(cloudNetworkAgent);
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {

            try {
                logger.info("[{}] Sending call for proposal to Server Agents", myAgent);
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                final ACLMessage proposal = new ACLMessage(CFP);
                try {
                    proposal.setContent(getMapper().writeValueAsString(job));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                search(myAgent, SA_SERVICE_TYPE).forEach(proposal::addReceiver);
                myAgent.send(proposal);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
