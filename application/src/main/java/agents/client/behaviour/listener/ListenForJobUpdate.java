package agents.client.behaviour.listener;

import static agents.client.ClientAgentConstants.MAX_TIME_DIFFERENCE;
import static utils.TimeUtils.getCurrentTime;
import static messages.domain.constants.MessageProtocolConstants.BACK_UP_POWER_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.DELAYED_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.FINISH_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.GREEN_POWER_JOB_PROTOCOL;
import static messages.domain.constants.MessageProtocolConstants.STARTED_JOB_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;

import agents.client.ClientAgent;

import com.gui.agents.ClientAgentNode;
import com.gui.agents.domain.JobStatusEnum;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Behaviour which handles the information that the job status is updated
 */
public class ListenForJobUpdate extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForJobUpdate.class);
	private static final MessageTemplate messageTemplate = and(
			or(or(or(MatchProtocol(FINISH_JOB_PROTOCOL), MatchProtocol(DELAYED_JOB_PROTOCOL)),
							or(MatchProtocol(BACK_UP_POWER_JOB_PROTOCOL), MatchProtocol(STARTED_JOB_PROTOCOL))),
					MatchProtocol(GREEN_POWER_JOB_PROTOCOL)),
			MatchPerformative(INFORM));

	private final ClientAgent myClientAgent;

	/**
	 * Behaviours constructor.
	 *
	 * @param clientAgent agent executing the behaviour
	 */
	public ListenForJobUpdate(final ClientAgent clientAgent) {
		super(clientAgent);
		this.myClientAgent = clientAgent;
	}

	/**
	 * Method which waits for messages informing about changes in the job's status
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(messageTemplate);
		if (Objects.nonNull(message)) {
			switch (message.getProtocol()) {
				case STARTED_JOB_PROTOCOL -> {
					checkIfJobStartedOnTime();
					((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.IN_PROGRESS);
				}
				case FINISH_JOB_PROTOCOL -> {
					checkIfJobFinishedOnTime();
					((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.FINISHED);
					myClientAgent.getGuiController().updateClientsCountByValue(-1);
					myClientAgent.doDelete();
				}
				case DELAYED_JOB_PROTOCOL -> {
					logger.info("[{}] The execution of my job has some delay! :(", myAgent.getName());
					((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.DELAYED);
				}
				case BACK_UP_POWER_JOB_PROTOCOL -> {
					logger.info("[{}] My job is being executed using the back up power!", myAgent.getName());
					((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.ON_BACK_UP);
				}
				case GREEN_POWER_JOB_PROTOCOL -> {
					logger.info("[{}] My job is again being executed using the green power!", myAgent.getName());
					((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.IN_PROGRESS);
				}
			}
		} else {
			block();
		}
	}

	private void checkIfJobStartedOnTime() {
		final OffsetDateTime startTime = getCurrentTime();
		final long timeDifference = ChronoUnit.MILLIS.between(myClientAgent.getSimulatedJobStart(), startTime);
		if (MAX_TIME_DIFFERENCE.isValidValue(timeDifference)) {
			logger.info("[{}] The execution of my job started on time! :)", myAgent.getName());
		} else {
			logger.info("[{}] The execution of my job started with a delay equal to {}! :(", myAgent.getName(),
					timeDifference);
		}
	}

	private void checkIfJobFinishedOnTime() {
		final OffsetDateTime endTime = getCurrentTime();
		final long timeDifference = ChronoUnit.MILLIS.between(endTime, myClientAgent.getSimulatedJobEnd());
		if (MAX_TIME_DIFFERENCE.isValidValue(timeDifference)) {
			logger.info("[{}] The execution of my job finished on time! :)", myAgent.getName());
		} else {
			logger.info("[{}] The execution of my job finished with a delay equal to {}! :(", myAgent.getName(),
					timeDifference);
		}
	}
}
