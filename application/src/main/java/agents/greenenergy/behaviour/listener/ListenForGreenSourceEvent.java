package agents.greenenergy.behaviour.listener;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powershortage.announcer.AnnouncePowerShortageFinish;
import agents.greenenergy.behaviour.powershortage.announcer.AnnounceSourcePowerShortage;
import com.gui.domain.event.AbstractEvent;
import com.gui.domain.event.PowerShortageEvent;
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
        final AbstractEvent event = myGreenEnergyAgent.getAgentNode().getEvent();
        if (Objects.nonNull(event)) {
            switch (event.getEventTypeEnum()) {
                case POWER_SHORTAGE -> {
                    final PowerShortageEvent powerShortageEvent = (PowerShortageEvent) event;
                    myGreenEnergyAgent.addBehaviour(new AnnounceSourcePowerShortage(myGreenEnergyAgent, event.getOccurrenceTime(), powerShortageEvent.getNewMaximumPower()));
                }
                case POWER_SHORTAGE_FINISH -> myGreenEnergyAgent.addBehaviour(new AnnouncePowerShortageFinish(myGreenEnergyAgent));
            }
            myGreenEnergyAgent.getAgentNode().setEvent(null);
        }

    }
}
