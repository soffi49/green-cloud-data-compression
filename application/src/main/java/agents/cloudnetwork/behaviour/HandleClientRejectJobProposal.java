package agents.cloudnetwork.behaviour;

import agents.client.message.SendJobMessage;
import agents.cloudnetwork.CloudNetworkAgent;
import com.fasterxml.jackson.databind.JsonMappingException;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

/**
 * Cyclic behaviour for Cloud Network Agent. It purpose is to handle the reject proposal response from Client
 */
public class HandleClientRejectJobProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleClientRejectJobProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(REJECT_PROPOSAL);

    private CloudNetworkAgent myCloudAgent;

    private HandleClientRejectJobProposal(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleClientRejectJobProposal createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleClientRejectJobProposal(cloudNetworkAgent);
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
            try {
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                logger.info("[{}] Client {} rejected the job proposal", myAgent, message.getSender().getLocalName());
                final AID serverToReject = myCloudAgent.getServerForJobMap().get(job);
                myCloudAgent.getServerForJobMap().remove(job);
                myCloudAgent.send(SendJobMessage.create(job, List.of(serverToReject), REJECT_PROPOSAL).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
