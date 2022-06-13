package agents.greenenergy.behaviour;

import static agents.greenenergy.DataStoreConstants.JOB_MESSAGE;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.CFP;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour responsible for handling server call for proposal for power necessary to execute the given job
 */
public class ReceivePowerRequest extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ReceivePowerRequest.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(CFP), MatchProtocol(SERVER_JOB_CFP_PROTOCOL));

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    /**
     * Behaviours constructor.
     *
     * @param myAgent agent which is executing the behaviour
     */
    private ReceivePowerRequest(Agent myAgent) {
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
        this.guid = myGreenEnergyAgent.getName();
    }

    /**
     * Method used to create the power receiving behaviour
     *
     * @param greenEnergyAgent agent which is executing the behaviour
     * @return created behaviour
     */
    public static ReceivePowerRequest createFor(GreenEnergyAgent greenEnergyAgent) {
        return new ReceivePowerRequest(greenEnergyAgent);
    }

    /**
     * Method which listens for the power call for proposals coming from the servers. It analyzes the request and either
     * rejects it or sends request to Monitoring Agent for the weather data.
     */
    @Override
    public void action() {
        final ACLMessage cfp = myAgent.receive(messageTemplate);

        if (Objects.nonNull(cfp)) {
            try {
                Job job = readJob(cfp);
                if (job.getPower() > myGreenEnergyAgent.getAvailableCapacity()) {
                    logger.info("[{}] Refusing job with id {} for client {} - not enough available power.", guid,
                                job.getJobId(), job.getClientIdentifier());
                    throw new RefuseException(cfp.getContent());
                }
                logger.info("[{}] Sending weather request to monitoring agent.", guid);
                requestMonitoringData(job, cfp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private Job readJob(ACLMessage callForProposal) throws NotUnderstoodException {
        try {
            return getMapper().readValue(callForProposal.getContent(), Job.class);
        } catch (JsonProcessingException e) {
            throw new NotUnderstoodException(e.getMessage());
        }
    }

    private void requestMonitoringData(Job job, ACLMessage cfp) {
        getDataStore().put(JOB_MESSAGE, job);

        var sequentialBehaviour = new SequentialBehaviour();
        sequentialBehaviour.setDataStore(getDataStore());
        sequentialBehaviour.addSubBehaviour(new RequestWeatherData(myGreenEnergyAgent, cfp.getConversationId()));
        sequentialBehaviour.addSubBehaviour(new ReceiveWeatherData(myGreenEnergyAgent, cfp, cfp.getConversationId()));

        myAgent.addBehaviour(sequentialBehaviour);
    }
}
