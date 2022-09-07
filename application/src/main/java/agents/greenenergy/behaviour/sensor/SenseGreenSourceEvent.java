package agents.greenenergy.behaviour.sensor;

import static agents.greenenergy.domain.GreenEnergyAgentConstants.GREEN_ENERGY_ENVIRONMENT_SENSOR_TIMEOUT;
import static domain.powershortage.PowerShortageCause.PHYSICAL_CAUSE;

import java.util.Objects;

import com.gui.event.domain.AbstractEvent;
import com.gui.event.domain.PowerShortageEvent;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceSourcePowerShortage;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceSourcePowerShortageFinish;
import jade.core.behaviours.TickerBehaviour;

/**
 * Behaviour listens for the outside world events
 */
public class SenseGreenSourceEvent extends TickerBehaviour {

	private final GreenEnergyAgent myGreenEnergyAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param myGreenEnergyAgent agent which is executing the behaviour
	 */
	public SenseGreenSourceEvent(final GreenEnergyAgent myGreenEnergyAgent) {
		super(myGreenEnergyAgent, GREEN_ENERGY_ENVIRONMENT_SENSOR_TIMEOUT);
		this.myGreenEnergyAgent = myGreenEnergyAgent;
	}

	/**
	 * Method verifies if some outside event has occurred
	 */
	@Override
	protected void onTick() {
		final AbstractEvent event = myGreenEnergyAgent.getAgentNode().removeEventFromStack();

		if (Objects.nonNull(event)) {
			switch (event.getEventTypeEnum()) {
				case POWER_SHORTAGE -> {
					final PowerShortageEvent powerShortageEvent = (PowerShortageEvent) event;

					if (powerShortageEvent.isIndicateFinish()) {
						myGreenEnergyAgent.addBehaviour(new AnnounceSourcePowerShortageFinish(myGreenEnergyAgent));
					} else {
						myGreenEnergyAgent.addBehaviour(
								new AnnounceSourcePowerShortage(myGreenEnergyAgent, null, event.getOccurrenceTime(),
										(double) powerShortageEvent.getNewMaximumPower(), PHYSICAL_CAUSE));
					}
				}
			}
		}

	}
}
