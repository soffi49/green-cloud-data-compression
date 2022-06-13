package agents.greenenergy;

import domain.job.Job;
import domain.location.Location;
import jade.core.AID;
import jade.core.Agent;
import java.util.Set;

/**
 * Abstract agent storing data of the Green Source Energy Agent
 */
public abstract class AbstractGreenEnergyAgent extends Agent {

    protected int availableCapacity;

    protected Location location;

    protected AID monitoringAgent;

    protected AID ownerServer;

    protected double pricePerPowerUnit;

    protected Set<Job> currentJobs;

    public AID getOwnerServer() {
        return ownerServer;
    }

    public void setOwnerServer(AID ownerServer) {
        this.ownerServer = ownerServer;
    }

    public double getPricePerPowerUnit() {
        return pricePerPowerUnit;
    }

    public void setPricePerPowerUnit(double pricePerPowerUnit) {
        this.pricePerPowerUnit = pricePerPowerUnit;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public Location getLocation() {
        return location;
    }

    public AID getMonitoringAgent() {
        return monitoringAgent;
    }

    public Set<Job> getCurrentJobs() {
        return currentJobs;
    }
}
