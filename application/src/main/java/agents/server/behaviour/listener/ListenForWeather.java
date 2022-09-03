package agents.server.behaviour.listener;

import static messages.domain.constants.MessageProtocolConstants.SERVER_JOB_START_CHECK_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static java.lang.Math.abs;
import static mapper.JsonMapper.getMapper;
import static messages.MessagingUtils.isMessageContentValid;

import agents.server.ServerAgent;
import agents.server.behaviour.StartJobExecution;

import com.fasterxml.jackson.core.JsonProcessingException;

import domain.job.CheckedPowerJob;
import domain.job.Job;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ListenForWeather extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForWeather.class);
	private static final MessageTemplate messageTemplate = and(or(MatchPerformative(INFORM), MatchPerformative(REFUSE)),
			MatchProtocol(SERVER_JOB_START_CHECK_PROTOCOL));

	private final ServerAgent myServerAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent agent that is executing the behaviour
	 */
	public ListenForWeather(Agent agent) {
		myServerAgent = (ServerAgent) agent;
	}

	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(messageTemplate);
		if (Objects.nonNull(message) && isMessageContentValid(message, CheckedPowerJob.class)) {
			CheckedPowerJob checkedPowerJob;
			try {
				checkedPowerJob = getMapper().readValue(message.getContent(), CheckedPowerJob.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

			var currentAvailableCapacity = myServerAgent.manage()
					.getAvailableCapacity(checkedPowerJob.getPowerJob().getStartTime(),
							checkedPowerJob.getPowerJob().getEndTime(), null, null);
			if (currentAvailableCapacity < 0) {
				logger.error("[{}] Exceeded available capacity by {}!",
						myServerAgent.getName(), abs(currentAvailableCapacity));
			}

			if (message.getPerformative() == INFORM) {
				final Job job = myServerAgent.manage().getJobByIdAndStartDate(checkedPowerJob.getPowerJob().getJobId(),
						checkedPowerJob.getPowerJob().getStartTime());
				if (Objects.nonNull(job)) {
					logger.info("[{}] Starting job execution!", myServerAgent.getName());
					myAgent.addBehaviour(
							StartJobExecution.createFor(myServerAgent, job, checkedPowerJob.informCNAStart(),
									checkedPowerJob.informCNAFinish()));
				} else {
					logger.info("[{}] Job {} must have been finished or transferred before its execution!",
							myServerAgent.getName(), checkedPowerJob.getPowerJob().getJobId());
				}
			} else if (message.getPerformative() == REFUSE) {
				logger.info("[{}] Aborting job execution!.", myServerAgent.getName());
			}
		} else {
			block();
		}
	}
}
