package agents.greenenergy.behaviour.powercheck;

import static common.constant.MessageProtocolConstants.SERVER_JOB_START_CHECK_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;

import com.fasterxml.jackson.core.JsonProcessingException;

import domain.job.CheckedPowerJob;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for initiating weather request from Monitoring Agent,
 * is a part of protocol responsible to double-check the weather before starting
 * the job execution
 */
public class ReceivePowerCheckRequest extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ReceivePowerCheckRequest.class);
	private static final MessageTemplate messageTemplate = and(MatchPerformative(REQUEST),
			MatchProtocol(SERVER_JOB_START_CHECK_PROTOCOL));

	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;

	/**
	 * Behaviours constructor.
	 *
	 * @param myAgent agent which is executing the behaviour
	 */
	public ReceivePowerCheckRequest(Agent myAgent) {
		this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Requests weather data from MonitoringAgent, via {@link RequestWeatherData} and
	 * {@link ReceiveWeatherData} behaviours
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(messageTemplate);
		if (Objects.nonNull(message)) {
			try {
				var checkedPowerJob = readJob(message);
				logger.info("[{}] Sending weather request to monitoring agent before job {} execution.", guid,
						checkedPowerJob.getPowerJob().getJobId());
				requestMonitoringData(message, checkedPowerJob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			block();
		}
	}

	private CheckedPowerJob readJob(ACLMessage message) throws NotUnderstoodException {
		try {
			return getMapper().readValue(message.getContent(), CheckedPowerJob.class);
		} catch (JsonProcessingException e) {
			throw new NotUnderstoodException(e.getMessage());
		}
	}

	private void requestMonitoringData(final ACLMessage message, final CheckedPowerJob job) {
		var sequentialBehaviour = new SequentialBehaviour();
		sequentialBehaviour.addSubBehaviour(
				new RequestWeatherData(myGreenEnergyAgent, message));
		sequentialBehaviour.addSubBehaviour(
				new ReceiveWeatherData(myGreenEnergyAgent, message, job, sequentialBehaviour));
		myAgent.addBehaviour(sequentialBehaviour);
	}
}
