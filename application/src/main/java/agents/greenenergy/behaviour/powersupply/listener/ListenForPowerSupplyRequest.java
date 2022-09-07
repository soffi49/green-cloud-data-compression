package agents.greenenergy.behaviour.powersupply.listener;

import static agents.greenenergy.behaviour.powersupply.listener.logs.PowerSupplyListenerLog.REQUEST_TO_MONITORING_AGENT_LOG;
import static agents.greenenergy.behaviour.powersupply.listener.template.PowerSupplyMessageTemplates.POWER_SUPPLY_REQUEST_TEMPLATE;
import static java.util.Objects.nonNull;
import static messages.MessagingUtils.readMessageContent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.weathercheck.listener.ListenForNewJobWeatherData;
import agents.greenenergy.behaviour.weathercheck.request.RequestWeatherData;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import messages.domain.factory.ReplyMessageFactory;

/**
 * Behaviour listens for the power supply request coming from parent Server Agent
 */
public class ListenForPowerSupplyRequest extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForPowerSupplyRequest.class);

	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;

	/**
	 * Behaviours constructor.
	 *
	 * @param myAgent agent which is executing the behaviour
	 */
	public ListenForPowerSupplyRequest(Agent myAgent) {
		this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
		this.guid = myGreenEnergyAgent.getName();
	}

	/**
	 * Method listens for the power CFP coming from the server.
	 * It analyzes the request and either rejects it or proceeds with request processing by sending
	 * another request to Monitoring Agent for the weather data.
	 */
	@Override
	public void action() {
		final ACLMessage cfp = myAgent.receive(POWER_SUPPLY_REQUEST_TEMPLATE);
		if (nonNull(cfp)) {
			final PowerJob job = readJob(cfp);
			if (nonNull(job)) {
				myGreenEnergyAgent.getPowerJobs().put(job, JobStatusEnum.PROCESSING);
				requestMonitoringData(cfp, job);
			}
		} else {
			block();
		}
	}

	private PowerJob readJob(ACLMessage callForProposal) {
		try {
			return readMessageContent(callForProposal, PowerJob.class);
		} catch (Exception e) {
			logger.info("[{}] I didn't understand the message from the server, refusing the job", guid);
			myAgent.send(ReplyMessageFactory.prepareRefuseReply(callForProposal.createReply()));
		}
		return null;
	}

	private void requestMonitoringData(final ACLMessage cfp, final PowerJob job) {
		var sequentialBehaviour = new SequentialBehaviour();
		sequentialBehaviour.addSubBehaviour(
				new RequestWeatherData(myGreenEnergyAgent, cfp.getProtocol(), cfp.getConversationId(), job));
		sequentialBehaviour.addSubBehaviour(
				new ListenForNewJobWeatherData(myGreenEnergyAgent, cfp, job, sequentialBehaviour));
		myAgent.addBehaviour(sequentialBehaviour);
	}
}
