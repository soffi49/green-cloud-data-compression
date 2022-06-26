package agents.cloudnetwork;

import agents.AbstractAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.Agent;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract agent class storing the data regarding Cloud Network Agent
 */
public abstract class AbstractCloudNetworkAgent extends AbstractAgent {

    protected Map<Job, JobStatusEnum> networkJobs;
    protected Map<String, AID> serverForJobMap;

    AbstractCloudNetworkAgent() {
        super.setup();
    }

    /**
     * Abstract Cloud Network Agent constructor.
     *
     * @param networkJobs     list of the jobs together with their statuses
     *                        that are being processed in the network
     * @param serverForJobMap map storing jobs and corresponding job's executor addresses
     */
    AbstractCloudNetworkAgent(Map<Job, JobStatusEnum> networkJobs, Map<String, AID> serverForJobMap) {
        this.serverForJobMap = serverForJobMap;
        this.networkJobs = networkJobs;
    }

    /**
     * Method run on agent start. It initializes the Cloud Network Agent data with default values
     */
    @Override
    protected void setup() {
        super.setup();

        serverForJobMap = new HashMap<>();
        networkJobs = new HashMap<>();
    }
    public int getPowerInUse(final OffsetDateTime startDate, final OffsetDateTime endDate) {
        return networkJobs.entrySet().stream()
                .filter(job -> !job.getValue().equals(JobStatusEnum.PROCESSING) &&
                        job.getKey().getStartTime().isBefore(endDate) &&
                        job.getKey().getEndTime().isAfter(startDate))
                .mapToInt(job -> job.getKey().getPower()).sum();
    }

    public Job getJobById(final String jobId) {
        return networkJobs.keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
    }

    public Map<String, AID> getServerForJobMap() {
        return serverForJobMap;
    }

    public void setServerForJobMap(Map<String, AID> serverForJobMap) {
        this.serverForJobMap = serverForJobMap;
    }

    public Map<Job, JobStatusEnum> getNetworkJobs() {
        return networkJobs;
    }

    public void setNetworkJobs(Map<Job, JobStatusEnum> networkJobs) {
        this.networkJobs = networkJobs;
    }
}
