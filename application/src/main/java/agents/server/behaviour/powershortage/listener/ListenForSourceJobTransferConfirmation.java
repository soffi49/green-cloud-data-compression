package agents.server.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL;
import static jade.lang.acl.ACLMessage.FAILURE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchContent;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.ReplyMessageFactory.prepareReply;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.transfer.PerformSourceJobTransfer;

import com.fasterxml.jackson.core.JsonProcessingException;

import domain.job.JobInstanceIdentifier;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Behaviour listens for the message which confirms the power transfer coming from the green source
 */
public class ListenForSourceJobTransferConfirmation extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForSourceJobTransferConfirmation.class);

	private final MessageTemplate messageTemplate;
	private final ServerAgent myServerAgent;
	private final JobInstanceIdentifier jobToTransfer;
	private final ACLMessage greenSourceRequest;

	/**
	 * Behaviours constructor
	 *
	 * @param agent              server executing the behaviour
	 * @param jobInstanceId      unique job instance identifier
	 * @param greenSourceRequest green source job transfer request message
	 */
	public ListenForSourceJobTransferConfirmation(ServerAgent agent,
			JobInstanceIdentifier jobInstanceId,
			ACLMessage greenSourceRequest) {
		super(agent);
		this.myServerAgent = agent;
		this.greenSourceRequest = greenSourceRequest;
		this.jobToTransfer = jobInstanceId;
		this.messageTemplate = createListenerTemplate(jobInstanceId);
	}

	/**
	 * Method listens for the confirmation message coming from Green Energy Source. When the confirmation is received,
	 * it schedules the transfer execution and sends the response to another green source which requested the transfer.
	 */
	@Override
	public void action() {
		final ACLMessage inform = myAgent.receive(messageTemplate);

		if (Objects.nonNull(inform)) {
			try {
				final String jobId = jobToTransfer.getJobId();
				if (Objects.nonNull(myServerAgent.manage().getJobById(jobId))) {
					logger.info("[{}] Scheduling the job {} transfer. Sending confirmation to green source",
							myAgent.getName(), jobId);
					displayMessageArrow(myServerAgent, greenSourceRequest.getSender());
					myAgent.addBehaviour(
							prepareBehaviour(jobToTransfer, inform.getSender(), greenSourceRequest.getSender()));
					myServerAgent.send(prepareReply(greenSourceRequest.createReply(), jobToTransfer, INFORM));
				} else {
					logger.info("[{}] Job execution finished before transfer", myAgent.getName());
					final ACLMessage finishJobMessage = prepareFinishMessage(jobId, jobToTransfer.getStartTime(),
							List.of(inform.getSender()));
					displayMessageArrow(myServerAgent, inform.getSender());
					myServerAgent.send(finishJobMessage);
					myServerAgent.send(prepareReply(greenSourceRequest.createReply(), jobToTransfer, FAILURE));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

	private static MessageTemplate createListenerTemplate(final JobInstanceIdentifier jobInstanceId) {
		try {
			final String expectedContent = getMapper().writeValueAsString(jobInstanceId);
			return and(MatchContent(expectedContent),
					and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_JOB_CONFIRMATION_PROTOCOL)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private ParallelBehaviour prepareBehaviour(final JobInstanceIdentifier jobInstanceId, final AID newGreenSource,
			final AID previousGreenSource) {
		final ParallelBehaviour behaviour = new ParallelBehaviour();
		behaviour.addSubBehaviour(PerformSourceJobTransfer.createFor(myServerAgent, jobInstanceId, newGreenSource));
		behaviour.addSubBehaviour(
				new ListenForSourceTransferCancellation(myAgent, newGreenSource, previousGreenSource));
		return behaviour;
	}
}
