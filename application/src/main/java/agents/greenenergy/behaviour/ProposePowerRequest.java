package agents.greenenergy.behaviour;

import static agents.greenenergy.domain.GreenEnergyAgentConstants.MAX_ERROR_IN_JOB_FINISH;
import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.convertToSimulationTime;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static messages.domain.ReplyMessageFactory.prepareStringReply;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour which is responsible for sending the proposal with power request to Server Agent and
 * handling the retrieved responses.
 */
public class ProposePowerRequest extends ProposeInitiator {

    private static final Logger logger = LoggerFactory.getLogger(ProposePowerRequest.class);
    private final String guid;
    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor.
     *
     * @param agent agent which is executing the behaviour
     * @param msg   proposal message that is sent to the Server Agent
     */
    public ProposePowerRequest(final Agent agent, final ACLMessage msg) {
        super(agent, msg);
        this.myGreenEnergyAgent = (GreenEnergyAgent) agent;
        this.guid = myGreenEnergyAgent.getName();
    }

    /**
     * Method handles the accept proposal response from server. It updates the state of the job in
     * green source data and sends the information that the execution of the given job can be started.
     *
     * @param accept_proposal accept proposal response retrieved from the Server Agent
     */
    @Override
    protected void handleAcceptProposal(final ACLMessage accept_proposal) {
        final String jobId = accept_proposal.getContent();
        final PowerJob job = myGreenEnergyAgent.getJobById(jobId);
        logger.info("[{}] Sending information back to server agent.", guid);
        myGreenEnergyAgent.getPowerJobs().replace(job, JobStatusEnum.ACCEPTED);
        var response = prepareStringReply(accept_proposal.createReply(), jobId, INFORM);
        response.setProtocol(SERVER_JOB_CFP_PROTOCOL);
        myAgent.addBehaviour(new ListenForUnfinishedJobs(myGreenEnergyAgent, calculateJobStartTimeout(job), job.getJobId()));
        displayMessageArrow(myGreenEnergyAgent, accept_proposal.getSender());
        myAgent.send(response);
    }

    /**
     * Method handles the reject proposal response from server. It logs the information to the console.
     *
     * @param reject_proposal reject proposal response retrieved from the Server Agent
     */
    @Override
    protected void handleRejectProposal(final ACLMessage reject_proposal) {
        logger.info("[{}] Server rejected the job proposal", guid);
    }

    private Long calculateJobStartTimeout(final PowerJob job) {
        final long hourDifferenceStart = ChronoUnit.SECONDS.between(getCurrentTime(), job.getStartTime());
        final long hourDifferenceExecution = ChronoUnit.SECONDS.between(job.getStartTime(), job.getEndTime());
        return convertToSimulationTime((hourDifferenceStart < 0 ? 0 : hourDifferenceStart) + hourDifferenceExecution) + MAX_ERROR_IN_JOB_FINISH;
    }
}
