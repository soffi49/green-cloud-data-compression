package agents.greenenergy.behaviour;

import agents.client.message.SendJobMessage;
import agents.greenenergy.GreenEnergyAgent;
import agents.server.ServerAgent;
import agents.server.behaviour.HandleCNARejectProposal;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

public class HandleServerRejectProposal extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleServerRejectProposal.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(REJECT_PROPOSAL);

    private HandleServerRejectProposal(final GreenEnergyAgent greenEnergyAgent) {
        super(greenEnergyAgent);
    }

    public static HandleServerRejectProposal createFor(final GreenEnergyAgent greenEnergyAgent) {
        return new HandleServerRejectProposal(greenEnergyAgent);
    }


    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                logger.info("[{}] Server rejected the job proposal", myAgent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
