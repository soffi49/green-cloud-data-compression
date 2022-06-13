package agents.cloudnetwork;

import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract agent class storing the data regarding Cloud Network Agent
 */
public abstract class AbstractCloudNetworkAgent extends Agent{

    protected List<Job> currentJobs;
    protected List<Job> futureJobs;
    protected Map<String, AID> serverForJobMap;
    protected int inUsePower;

    AbstractCloudNetworkAgent() {
    }

    /**
     * Abstract Cloud Network Agent constructor.
     *
     * @param currentJobs list of the jobs that are being executed currently in the network
     * @param futureJobs list of the jobs that are planned to be executed in the network
     * @param serverForJobMap map storing jobs and corresponding job's executor addresses
     * @param inUsePower current network in use power
     */
    AbstractCloudNetworkAgent(List<Job> currentJobs, List<Job> futureJobs, Map<String, AID> serverForJobMap, int inUsePower) {
        this.currentJobs = currentJobs;
        this.futureJobs = futureJobs;
        this.serverForJobMap = serverForJobMap;
        this.inUsePower = inUsePower;
    }

    /**
     * Method run on agent start. It initializes the Cloud Network Agent data with default values
     */
    @Override
    protected void setup() {
        super.setup();

        inUsePower = 0;
        currentJobs = new ArrayList<>();
        futureJobs = new ArrayList<>();
        serverForJobMap = new HashMap<>();
    }

    public Map<String, AID> getServerForJobMap() {
        return serverForJobMap;
    }

    public void setServerForJobMap(Map<String, AID> serverForJobMap) {
        this.serverForJobMap = serverForJobMap;
    }

    public List<Job> getCurrentJobs() {
        return currentJobs;
    }

    public void setCurrentJobs(List<Job> currentJobs) {
        this.currentJobs = currentJobs;
    }

    public List<Job> getFutureJobs() {
        return futureJobs;
    }

    public void setFutureJobs(List<Job> futureJobs) {
        this.futureJobs = futureJobs;
    }

    public int getInUsePower() {
        return inUsePower;
    }

    public void setInUsePower(int inUsePower) {
        this.inUsePower = inUsePower;
    }
}
