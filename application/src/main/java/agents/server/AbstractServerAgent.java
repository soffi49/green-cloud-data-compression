package agents.server;

import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractServerAgent extends Agent {

    protected Set<Job> currentJobs;
    protected Map<Job, AID> greenSourceForJobMap;
    protected AID ownerCloudNetworkAgent;
    protected int messagesSentCount;
    protected double pricePerHour;
    protected int powerInUse;
    protected int availableCapacity;

    AbstractServerAgent() {
        super.setup();

        currentJobs = new HashSet<>();
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


    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public AID getOwnerCloudNetworkAgent() {
        return ownerCloudNetworkAgent;
    }

    public void setMessagesSentCount(int messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
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

}
