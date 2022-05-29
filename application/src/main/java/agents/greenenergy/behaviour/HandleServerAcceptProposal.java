package agents.greenenergy.behaviour;

import common.message.SendJobMessage;
import agents.greenenergy.GreenEnergyAgent;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static jade.lang.acl.ACLMessage.INFORM;
import static mapper.JsonMapper.getMapper;

public class HandleServerAcceptProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleServerAcceptProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACCEPT_PROPOSAL);

    private GreenEnergyAgent greenEnergyAgent;

    private HandleServerAcceptProposal(final GreenEnergyAgent greenEnergyAgent) {
        super(greenEnergyAgent);
    }

    public static HandleServerAcceptProposal createFor(final GreenEnergyAgent greenEnergyAgent) {
        return new HandleServerAcceptProposal(greenEnergyAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        greenEnergyAgent = (GreenEnergyAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                logger.info("[{}] Sending information back to server agent", myAgent);
                greenEnergyAgent.getCurrentJobs().add(job);
                greenEnergyAgent.setAvailableCapacity(greenEnergyAgent.getAvailableCapacity() - job.getPower());
                myAgent.send(SendJobMessage.create(job, List.of(message.getSender()), INFORM).getMessage());
                myAgent.addBehaviour(HandleServerInformJobDone.createFor(greenEnergyAgent));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
