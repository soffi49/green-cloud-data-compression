package agents.greenenergy.behaviour;

import static agents.greenenergy.DataStoreConstants.JOB_MESSAGE;
import static agents.server.message.ReplyMessageFactory.prepareReply;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.*;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.GreenSourceData;
import domain.job.Job;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for handling server call for proposal for given job
 */
public class ReceivePowerRequest extends ContractNetResponder {

    private static final Logger logger = LoggerFactory.getLogger(ReceivePowerRequest.class);

    private final GreenEnergyAgent myGreenEnergyAgent;
    private final String guid;

    public ReceivePowerRequest(Agent a, MessageTemplate mt) {
        super(a, mt);
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
        this.guid = myGreenEnergyAgent.getName();
    }

    public static ReceivePowerRequest createFor(GreenEnergyAgent greenEnergyAgent) {
        return new ReceivePowerRequest(greenEnergyAgent, null);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, NotUnderstoodException {
        Job job = readJob(cfp);
        if (job.getPower() > myGreenEnergyAgent.getAvailableCapacity()) {
            logger.info("[{}] Refusing job with id {} for client {} - not enough available power.", guid,
                job.getJobId(), job.getClientIdentifier());
            throw new RefuseException(cfp.getContent());
        }
        logger.info("[{}] Sending weather request to monitoring agent.", guid);
        requestMonitoringData(job);
        logger.info("[{}] Replying with propose message to server.", guid);
        return prepareReply(cfp, getResponseData(job), PROPOSE);
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
        throws FailureException {
        Job job = readAcceptedJob(accept);
        logger.info("[{}] Sending information back to server agent.", guid);
        myGreenEnergyAgent.getCurrentJobs().add(job);
        myGreenEnergyAgent.setAvailableCapacity(myGreenEnergyAgent.getAvailableCapacity() - job.getPower());
        return prepareReply(accept, job, INFORM);
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        logger.info("[{}] Server rejected the job proposal", guid);
    }

    private Job readJob(ACLMessage callForProposal) throws NotUnderstoodException {
        try {
            return getMapper().readValue(callForProposal.getContent(), Job.class);
        } catch (JsonProcessingException e) {
            throw new NotUnderstoodException(e.getMessage());
        }
    }

    private Job readAcceptedJob(ACLMessage callForProposal) throws FailureException {
        try {
            return getMapper().readValue(callForProposal.getContent(), Job.class);
        } catch (JsonProcessingException e) {
            throw new FailureException(e.getMessage());
        }
    }

    private void requestMonitoringData(Job job) {
        getDataStore().put(JOB_MESSAGE, job);
        var requestData = new RequestWeatherData(myGreenEnergyAgent);
        registerAsChild(requestData);
        requestData.action();
        getChildren().remove(requestData);
    }

    private GreenSourceData getResponseData(Job job) throws RefuseException {
        var receiveWeatherData = new ReceiveWeatherData(myGreenEnergyAgent);
        registerAsChild(receiveWeatherData);
        while (!getDataStore().containsKey(job.getJobId())) {
            receiveWeatherData.action();
        }
        getChildren().remove(receiveWeatherData);
        if (getDataStore().get(job.getJobId()) instanceof String refuseMessage) {
            throw new RefuseException(refuseMessage);
        }
        return (GreenSourceData) getDataStore().get(job.getJobId());
    }
}
