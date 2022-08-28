package agents.greenenergy.behaviour.powercheck.jobstart;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.SERVER_JOB_START_CHECK_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static java.util.Objects.nonNull;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceWeatherPowerShortage;
import domain.MonitoringData;
import domain.job.CheckedPowerJob;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import messages.domain.factory.ReplyMessageFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour which is responsible for listening for the Monitoring Agent's response with weather data.
 */
public class ReceiveWeatherDataForJobStart extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ReceiveWeatherDataForJobStart.class);

	private final String guid;
	private final MessageTemplate messageTemplate;
	private final GreenEnergyAgent myGreenEnergyAgent;
	private final ACLMessage originalMessage;
	private final CheckedPowerJob checkedPowerJob;
	private final SequentialBehaviour parentBehaviour;

	/**
	 * Behaviour constructor.
	 *
	 * @param myGreenAgent agent which is executing the behaviour
	 */
	public ReceiveWeatherDataForJobStart(GreenEnergyAgent myGreenAgent, ACLMessage originalMessage,
			CheckedPowerJob checkedPowerJob, SequentialBehaviour sequentialBehaviour) {
		this.myGreenEnergyAgent = myGreenAgent;
		this.messageTemplate = and(MatchProtocol(SERVER_JOB_START_CHECK_PROTOCOL),
				and(or(MatchPerformative(INFORM), MatchPerformative(REFUSE)),
						MatchConversationId(originalMessage.getConversationId())));
		this.guid = myGreenEnergyAgent.getName();
		this.originalMessage = originalMessage;
		this.checkedPowerJob = checkedPowerJob;
		this.parentBehaviour = sequentialBehaviour;
	}

	/**
	 * Method responsible for listening for the Monitoring Agent reply. It waits for the reply, then processes the
	 * received weather information, re-checks the available power and informs the server about the outcome
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(messageTemplate);
		if (nonNull(message)) {
			final MonitoringData data = myGreenEnergyAgent.manage().readMonitoringData(message, originalMessage);
			if (nonNull(data)) {
				switch (message.getPerformative()) {
					case ACLMessage.REFUSE -> myGreenEnergyAgent.manage().handleRefuse(originalMessage,
							checkedPowerJob.getPowerJob());
					case INFORM -> handleInform(data);
				}
			}
			myAgent.removeBehaviour(parentBehaviour);
		} else {
			block();
		}
	}

	private void handleInform(final MonitoringData data) {
		var powerJob = checkedPowerJob.getPowerJob();
		double availablePower = myGreenEnergyAgent.manage()
				.getAverageAvailablePowerCheck(powerJob, data).orElse(0.0);

		if (availablePower <= 0.0) {
			logger.info(
					"[{}] Weather has changed before executing job with id {} - not enough available power. Needed {}, available {}",
					guid, powerJob.getJobId(), powerJob.getPower(), availablePower);
			displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getOwnerServer());
			myAgent.send(ReplyMessageFactory.prepareReply(originalMessage.createReply(), checkedPowerJob, REFUSE));
			var currentCapacity = myGreenEnergyAgent.getCapacity(data, getCurrentTime().toInstant());
			myAgent.addBehaviour(
					new AnnounceWeatherPowerShortage(myGreenEnergyAgent,
							checkedPowerJob.getPowerJob(),
							getCurrentTime(),
							currentCapacity));
		} else {
			logger.info("[{}] Everything okay - continuing job {} execution!", guid, powerJob.getJobId());
			displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getOwnerServer());
			myAgent.send(ReplyMessageFactory.prepareReply(originalMessage.createReply(), checkedPowerJob, INFORM));
		}
	}
}

