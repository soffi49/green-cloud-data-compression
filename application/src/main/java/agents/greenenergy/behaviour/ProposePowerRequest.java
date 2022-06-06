package agents.greenenergy.behaviour;

import static agents.server.message.ReplyMessageFactory.prepareReply;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.Job;
import exception.IncorrectJobStructureException;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProposePowerRequest extends ProposeInitiator {

    private static final Logger logger = LoggerFactory.getLogger(ProposePowerRequest.class);
    private final String guid;
    private final GreenEnergyAgent myGreenEnergyAgent;

    public ProposePowerRequest(final Agent a, final ACLMessage msg, final DataStore dataStore) {
        super(a, msg, dataStore);
        this.myGreenEnergyAgent = (GreenEnergyAgent) a;
        this.guid = myGreenEnergyAgent.getName();
    }

    @Override
    protected void handleAcceptProposal(final ACLMessage accept_proposal) {
        Job job;
        try {
            job = readAcceptedJob(accept_proposal);
        } catch (FailureException e) {
            throw new IncorrectJobStructureException();
        }
        logger.info("[{}] Sending information back to server agent.", guid);
        myGreenEnergyAgent.getCurrentJobs().add(job);
        myGreenEnergyAgent.setAvailableCapacity(myGreenEnergyAgent.getAvailableCapacity() - job.getPower());
        var response = prepareReply(accept_proposal, job, INFORM);
        response.setProtocol(SERVER_JOB_CFP_PROTOCOL);
        myAgent.send(response);
    }

    @Override
    protected void handleRejectProposal(final ACLMessage reject_proposal) {
        logger.info("[{}] Server rejected the job proposal", guid);
    }

    private Job readAcceptedJob(ACLMessage callForProposal) throws FailureException {
        try {
            return getMapper().readValue(callForProposal.getContent(), Job.class);
        } catch (JsonProcessingException e) {
            throw new FailureException(e.getMessage());
        }
    }
}
