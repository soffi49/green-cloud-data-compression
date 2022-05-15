package agents.greenenergy;

import domain.LocationData;
import jade.core.AID;
import jade.core.Agent;

public abstract class AbstractGreenEnergyAgent extends Agent {

    protected int availableCapacity;

    protected LocationData location;

    protected AID monitoringAgent;

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public LocationData getLocation(){ return location; }

    public void setLocation(LocationData location){ this.location = location; }

    public AID getMonitoringAgent(){ return monitoringAgent; }

    public void setMonitoringAgent(AID monitoringAgent){ this.monitoringAgent = monitoringAgent; }
}
