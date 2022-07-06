package agents.cloudnetwork;

import agents.AbstractAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.AID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract agent class storing the data regarding Cloud Network Agent
 */
public abstract class AbstractCloudNetworkAgent extends AbstractAgent {

    protected Map<Job, JobStatusEnum> networkJobs;
    protected Map<String, AID> serverForJobMap;
    protected Map<String, Integer> jobRequestRetries;
    protected AtomicLong completedJobs;

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
        jobRequestRetries = new HashMap<>();
        completedJobs = new AtomicLong(0L);
    }

    /**
     * Method calculates the power in use at the given moment
     *
     * @return current power in use
     */
    public int getCurrentPowerInUse() {
        return networkJobs.entrySet().stream()
                .filter(job -> job.getValue().equals(JobStatusEnum.IN_PROGRESS))
                .mapToInt(job -> job.getKey().getPower()).sum();
    }

    /**
     * Method retrieves the job by the job id from job map
     *
     * @param jobId job identifier
     * @return job
     */
    public Job getJobById(final String jobId) {
        return networkJobs.keySet().stream()
            .filter(job -> job.getJobId().equals(jobId))
            .findFirst()
            .orElse(null);
    }

    public Map<String, AID> getServerForJobMap() {
        return serverForJobMap;
    }

    public Map<Job, JobStatusEnum> getNetworkJobs() {
        return networkJobs;
    }

    public Map<String, Integer> getJobRequestRetries() {
        return jobRequestRetries;
    }

    public Long completedJob() {
        return completedJobs.incrementAndGet();
    }
}
