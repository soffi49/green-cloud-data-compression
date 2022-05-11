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
    protected Map<AID, GreenSourceData> acceptingGreenSources;
    protected List<AID> greenSourceAgentsList;
    protected AID ownerCloudNetworkAgent;
    protected int messagesSentCount;
    protected int responsesReceivedCount;
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

    public Map<AID, GreenSourceData> getAcceptingGreenSources() {
        return acceptingGreenSources;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public int getResponsesReceivedCount() {
        return responsesReceivedCount;
    }

    public void setResponsesReceivedCount(int responsesReceivedCount) {
        this.responsesReceivedCount = responsesReceivedCount;
    }

    public AID getOwnerCloudNetworkAgent() {
        return ownerCloudNetworkAgent;
    }
}
