package agents.server;

import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;

import java.util.*;

public abstract class AbstractServerAgent extends Agent {

    protected Set<Job> currentJobs;
    protected Map<Job, AID> greenSourceForJobMap;
    protected List<AID> ownedGreenSources;
    protected AID ownerCloudNetworkAgent;
    protected double pricePerHour;
    protected int powerInUse;
    protected int availableCapacity;

    AbstractServerAgent() {
        super.setup();

        currentJobs = new HashSet<>();
        ownedGreenSources = new ArrayList<>();
        powerInUse = 0;
    }

    public Map<Job, AID> getGreenSourceForJobMap() {
        return greenSourceForJobMap;
    }

    public void setGreenSourceForJobMap(Map<Job, AID> greenSourceForJobMap) {
        this.greenSourceForJobMap = greenSourceForJobMap;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public int getPowerInUse() {
        return powerInUse;
    }

    public AID getOwnerCloudNetworkAgent() {
        return ownerCloudNetworkAgent;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setPowerInUse(int powerInUse) {
        this.powerInUse = powerInUse;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public Set<Job> getCurrentJobs() {
        return currentJobs;
    }

    public void setCurrentJobs(Set<Job> currentJobs) {
        this.currentJobs = currentJobs;
    }

    public List<AID> getOwnedGreenSources() {
        return ownedGreenSources;
    }

    public void setOwnedGreenSources(List<AID> ownedGreenSources) {
        this.ownedGreenSources = ownedGreenSources;
    }

    public void setOwnerCloudNetworkAgent(AID ownerCloudNetworkAgent) {
        this.ownerCloudNetworkAgent = ownerCloudNetworkAgent;
    }
}
