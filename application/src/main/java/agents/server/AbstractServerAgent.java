package agents.server;

import domain.GreenSourceData;
import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractServerAgent extends Agent {

    protected List<Job> currentJobs;
    protected List<AID> greenSourceAgentsList;
    //TODO figure out how to NOT store chosen GS (may be different depending on client)
    protected AID chosenGreenSource;
    protected AID ownerCloudNetworkAgent;
    protected int messagesSentCount;
    protected double pricePerHour;
    protected int powerInUse;
    protected int availableCapacity;

    AbstractServerAgent() {
        super.setup();

        currentJobs = new ArrayList<>();
        powerInUse = 0;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public int getPowerInUse() {
        return powerInUse;
    }

    public List<AID> getGreenSourceAgentsList() {
        return greenSourceAgentsList;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public AID getOwnerCloudNetworkAgent() {
        return ownerCloudNetworkAgent;
    }

    public void setGreenSourceAgentsList(List<AID> greenSourceAgentsList) {
        this.greenSourceAgentsList = greenSourceAgentsList;
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

    public AID getChosenGreenSource() {
        return chosenGreenSource;
    }

    public void setChosenGreenSource(AID chosenGreenSource) {
        this.chosenGreenSource = chosenGreenSource;
    }
}
