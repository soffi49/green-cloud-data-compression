package agents.server.behaviour.listener;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.announcer.AnnounceServerPowerShortage;
import agents.server.behaviour.powershortage.announcer.AnnounceServerPowerShortageFinish;

import com.gui.event.domain.AbstractEvent;
import com.gui.event.domain.PowerShortageEvent;

import jade.core.behaviours.TickerBehaviour;

import java.util.Objects;

/**
 * Behaviour is responsible for listening for the outside world events
 */
public class ListenForServerEvent extends TickerBehaviour {

	private static final int TICK_TIMEOUT = 100;
	private final ServerAgent myServerAgent;

	/**
	 * Behaviour constructor.
	 *
	 * @param myServerAgent agent which is executing the behaviour
	 */
	public ListenForServerEvent(final ServerAgent myServerAgent) {
		super(myServerAgent, TICK_TIMEOUT);
		this.myServerAgent = myServerAgent;
	}

	/**
	 * Method verifies if some outside event has occurred
	 */
	@Override
	protected void onTick() {
		final AbstractEvent event = myServerAgent.getAgentNode().removeEventFromStack();
		if (Objects.nonNull(event)) {
			switch (event.getEventTypeEnum()) {
				case POWER_SHORTAGE -> {
					final PowerShortageEvent powerShortageEvent = (PowerShortageEvent) event;
					if (powerShortageEvent.isIndicateFinish()) {
						myServerAgent.addBehaviour(new AnnounceServerPowerShortageFinish(myServerAgent));
					} else {
						myServerAgent.addBehaviour(
								new AnnounceServerPowerShortage(myServerAgent, event.getOccurrenceTime(),
										powerShortageEvent.getNewMaximumPower()));
					}
				}
			}
		}
	}
}
