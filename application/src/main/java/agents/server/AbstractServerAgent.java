package agents.server;

import static mapper.JsonMapper.getMapper;

import agents.AbstractAgent;
import agents.server.domain.ServerStateManagement;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.GreenSourceData;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract agent class storing data of the Server Agent
 */
public abstract class AbstractServerAgent extends AbstractAgent {

    protected int initialMaximumCapacity;
    protected transient ServerStateManagement stateManagement;
    protected double pricePerHour;
    protected int currentMaximumCapacity;
    protected Map<Job, JobStatusEnum> serverJobs;
    protected Map<String, AID> greenSourceForJobMap;
    protected List<AID> ownedGreenSources;
    protected AID ownerCloudNetworkAgent;

    AbstractServerAgent() {
        super.setup();

        initialMaximumCapacity = 0;
        serverJobs = new HashMap<>();
        ownedGreenSources = new ArrayList<>();
        greenSourceForJobMap = new HashMap<>();
    }

    /**
     * Method chooses the green source for job execution
     *
     * @param greenSourceOffers offers from green sources
     * @return chosen offer
     */
    public ACLMessage chooseGreenSourceToExecuteJob(final List<ACLMessage> greenSourceOffers) {
        final Comparator<ACLMessage> compareGreenSources =
                Comparator.comparingDouble(
                        greenSource -> {
                            try {
                                return getMapper()
                                        .readValue(greenSource.getContent(), GreenSourceData.class)
                                        .getAvailablePowerInTime();
                            } catch (final JsonProcessingException e) {
                                return Double.MAX_VALUE;
                            }
                        });
        return greenSourceOffers.stream().min(compareGreenSources).orElseThrow();
    }

    public int getInitialMaximumCapacity() {
        return initialMaximumCapacity;
    }

    public int getCurrentMaximumCapacity() {
        return currentMaximumCapacity;
    }

    public void setCurrentMaximumCapacity(int currentMaximumCapacity) {
        this.currentMaximumCapacity = currentMaximumCapacity;
    }

    public AID getOwnerCloudNetworkAgent() {
        return ownerCloudNetworkAgent;
    }

    public void setOwnerCloudNetworkAgent(AID ownerCloudNetworkAgent) {
        this.ownerCloudNetworkAgent = ownerCloudNetworkAgent;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Map<Job, JobStatusEnum> getServerJobs() {
        return serverJobs;
    }

    public void setServerJobs(Map<Job, JobStatusEnum> serverJobs) {
        this.serverJobs = serverJobs;
    }

    public List<AID> getOwnedGreenSources() {
        return ownedGreenSources;
    }

    public void setOwnedGreenSources(List<AID> ownedGreenSources) {
        this.ownedGreenSources = ownedGreenSources;
    }

    public Map<String, AID> getGreenSourceForJobMap() {
        return greenSourceForJobMap;
    }

    public void setGreenSourceForJobMap(Map<String, AID> greenSourceForJobMap) {
        this.greenSourceForJobMap = greenSourceForJobMap;
    }

    public ServerStateManagement manage() {
        return stateManagement;
    }
}
