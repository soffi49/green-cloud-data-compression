package agents.server.behaviour.powershortage.listener.source;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.handler.TransferJobToGreenSource;
import domain.job.JobInstanceIdentifier;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour listens for the message which confirms the power transfer that will come from the green source agent
 */
public class ListenForSourceTransferConfirmation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForSourceTransferConfirmation.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL));

    private ServerAgent myServerAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        this.myServerAgent = (ServerAgent) myAgent;
    }

    /**
     * Method listens for the confirmation message coming from Green Energy Source. When the confirmation is received,
     * it schedules the transfer execution
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final JobInstanceIdentifier jobInstanceId = getMapper().readValue(inform.getContent(), JobInstanceIdentifier.class);
                final String jobId = jobInstanceId.getJobId();
                if (Objects.nonNull(myServerAgent.manage().getJobById(jobId))) {
                    logger.info("[{}] Scheduling the job transfer", myAgent.getName());
                    final AID previousAgent = myServerAgent.getGreenSourceForJobMap().get(jobId);
                    displayMessageArrow(myServerAgent, previousAgent);
                    myServerAgent.send(prepareJobPowerShortageInformation(jobInstanceId, jobInstanceId.getStartTime(), previousAgent, POWER_SHORTAGE_SOURCE_TRANSFER_PROTOCOL));
                    myAgent.addBehaviour(prepareBehaviour(jobInstanceId, inform.getSender(), previousAgent));
                } else {
                    logger.info("[{}] Job execution finished before transfer", myAgent.getName());
                    final ACLMessage finishJobMessage = prepareFinishMessage(jobId, jobInstanceId.getStartTime(), List.of(inform.getSender()));
                    displayMessageArrow(myServerAgent, inform.getSender());
                    myServerAgent.send(finishJobMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private ParallelBehaviour prepareBehaviour(final JobInstanceIdentifier jobInstanceId,  final AID newGreenSource, final AID previousGreenSource) {
        final ParallelBehaviour behaviour = new ParallelBehaviour();
        behaviour.addSubBehaviour(TransferJobToGreenSource.createFor(myServerAgent, jobInstanceId, newGreenSource));
        behaviour.addSubBehaviour(new ListenForSourceTransferCancellation(myAgent, newGreenSource, previousGreenSource));
        return behaviour;
    }
}
