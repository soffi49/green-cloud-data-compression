package agents.greenenergy.behaviour.listener;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceSourcePowerShortage;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceSourcePowerShortageFinish;
import com.gui.event.domain.AbstractEvent;
import com.gui.event.domain.PowerShortageEvent;
import jade.core.behaviours.TickerBehaviour;

import java.util.Objects;

/**
 * Behaviour is responsible for listening for the outside world events
 */
public class ListenForGreenSourceEvent extends TickerBehaviour {

    private static final int TICK_TIMEOUT = 100;
    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor.
     *
     * @param myGreenEnergyAgent agent which is executing the behaviour
     */
    public ListenForGreenSourceEvent(final GreenEnergyAgent myGreenEnergyAgent) {
        super(myGreenEnergyAgent, TICK_TIMEOUT);
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
                        myGreenEnergyAgent.addBehaviour(new AnnounceSourcePowerShortage(myGreenEnergyAgent, event.getOccurrenceTime(), powerShortageEvent.getNewMaximumPower()));
                    }
                }
            }
        }

    }
}
