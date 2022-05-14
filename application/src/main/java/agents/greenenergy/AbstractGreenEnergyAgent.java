package agents.greenenergy;

import jade.core.Agent;

public abstract class AbstractGreenEnergyAgent extends Agent {

    protected int availableCapacity;

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }
}
