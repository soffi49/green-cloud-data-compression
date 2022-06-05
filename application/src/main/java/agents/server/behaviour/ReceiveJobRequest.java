package agents.server.behaviour;

import static common.constant.MessageProtocolConstants.CNA_JOB_CFP_PROTOCOL;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import common.message.SendJobCallForProposalMessage;
import common.message.SendRefuseProposalMessage;
import domain.job.Job;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviours responsible for handling retrieval of CNA's job requests
 */
public class ReceiveJobRequest extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveJobRequest.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(CFP), MatchProtocol(CNA_JOB_CFP_PROTOCOL));

    private ServerAgent myServerAgent;

    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            try {
                if(myServerAgent.getOwnedGreenSources().isEmpty()) {
                    logger.info("I don't have the Green Source Agents");
                    myAgent.doDelete();
                }
                final Job job = getMapper().readValue(message.getContent(), Job.class);

                if (job.getPower() + myServerAgent.getPowerInUse() <= myServerAgent.getAvailableCapacity()) {
                    logger.info("[{}] Sending call for proposal to Green Source Agents", myAgent);
                    getDataStore().put(message.getSender(), message.createReply());
                    final ACLMessage cfp = SendJobCallForProposalMessage.create(job, myServerAgent.getOwnedGreenSources(), SERVER_JOB_CFP_PROTOCOL).getMessage();
                    myAgent.addBehaviour(new AnnouncePowerRequest(myAgent, cfp, getDataStore()));
                } else {
                    logger.info("[{}] Not enough available power! Sending refuse message to Cloud Network Agent", myAgent);
                    myAgent.send(SendRefuseProposalMessage.create(message.createReply()).getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
