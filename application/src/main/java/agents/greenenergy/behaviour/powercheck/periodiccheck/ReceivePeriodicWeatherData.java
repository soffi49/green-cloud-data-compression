package agents.greenenergy.behaviour.powercheck.periodiccheck;

import static common.TimeUtils.getCurrentTime;
import static domain.powershortage.PowerShortageCause.WEATHER_CAUSE;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.ACLMessage.REFUSE;
import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;
import static java.util.Objects.nonNull;
import static mapper.JsonMapper.getMapper;
import static messages.domain.constants.MessageProtocolConstants.PERIODIC_WEATHER_CHECK_PROTOCOL;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceSourcePowerShortage;
import domain.MonitoringData;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Behaviour responsible for receiving the current weather data and checking if the power did not dropped
 */
public class ReceivePeriodicWeatherData extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ReceivePeriodicWeatherData.class);

	private final MessageTemplate messageTemplate;
	private final GreenEnergyAgent myGreenEnergyAgent;
	private final String guid;

	private final Behaviour parentBehaviour;

	/**
	 * Behaviour constructor.
	 *
	 * @param myGreenAgent agent which is executing the behaviour
	 */
	public ReceivePeriodicWeatherData(GreenEnergyAgent myGreenAgent, Behaviour parentBehaviour) {
		this.myGreenEnergyAgent = myGreenAgent;
		this.messageTemplate = and(
				and(MatchProtocol(PERIODIC_WEATHER_CHECK_PROTOCOL), MatchSender(myGreenAgent.getMonitoringAgent())),
				and(or(MatchPerformative(INFORM), MatchPerformative(REFUSE)),
						MatchConversationId(PERIODIC_WEATHER_CHECK_PROTOCOL)));
		this.guid = myGreenEnergyAgent.getName();
		this.parentBehaviour = parentBehaviour;
	}

	/**
	 * Method responsible for listening for the Monitoring Agent reply. It waits for the reply, then processes the
	 * received weather information and verifies if the power hasn't dropped
	 */
	@Override
	public void action() {
		final ACLMessage message = myAgent.receive(messageTemplate);
		if (nonNull(message)) {
			try {
				final MonitoringData data = getMapper().readValue(message.getContent(), MonitoringData.class);
				if (nonNull(data)) {
					switch (message.getPerformative()) {
						case ACLMessage.REFUSE -> handleRefuse();
						case INFORM -> handleInform(data);
					}
				}
			} catch (JsonProcessingException e) {
				logger.info("[{}] I didn't understand the response with the weather data",
						myGreenEnergyAgent.getName());
			}
			myAgent.removeBehaviour(parentBehaviour);
		} else {
			block();
		}
	}

	private void handleInform(final MonitoringData data) {
		final OffsetDateTime time = getCurrentTime();
		logger.info("[{}] Received the weather data at {}", guid, time);
		final double availablePower = myGreenEnergyAgent.manage().getAvailablePower(getCurrentTime(), data).orElse(0.0);
		if (availablePower < myGreenEnergyAgent.manage().getCurrentPowerInUseForGreenSource()) {
			logger.info("[{}] There was a power drop at {}! Scheduling job transferring behaviour!", guid, time);
			myAgent.addBehaviour(
					new AnnounceSourcePowerShortage(myGreenEnergyAgent, null, getCurrentTime(), availablePower,
							WEATHER_CAUSE));
		} else {
			logger.info("[{}] Power has not dropped. Continuing jobs execution", myGreenEnergyAgent.getLocalName());
		}
	}

	private void handleRefuse() {
		logger.info("[{}] The weather data is not available at {}", guid, getCurrentTime());
	}
}
