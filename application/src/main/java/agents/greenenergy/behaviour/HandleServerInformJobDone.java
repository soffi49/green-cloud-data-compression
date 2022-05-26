package agents.greenenergy.behaviour;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static mapper.JsonMapper.getMapper;

public class HandleServerInformJobDone extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleServerInformJobDone.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.and(MatchPerformative(INFORM), MatchConversationId("FINISHED"));

    private GreenEnergyAgent greenEnergyAgent;

    private HandleServerInformJobDone(final GreenEnergyAgent greenEnergyAgent) {
        super(greenEnergyAgent);
    }

    public static HandleServerInformJobDone createFor(final GreenEnergyAgent greenEnergyAgent) {
        return new HandleServerInformJobDone(greenEnergyAgent);
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
                logger.info("[{}] Finish the execution of the job", myAgent);
                greenEnergyAgent.getCurrentJobs().remove(job);
                greenEnergyAgent.setAvailableCapacity(greenEnergyAgent.getAvailableCapacity() + job.getPower());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
