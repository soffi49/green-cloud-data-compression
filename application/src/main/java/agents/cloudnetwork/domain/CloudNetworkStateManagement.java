package agents.cloudnetwork.domain;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;

/**
 * Set of utilities used to manage the state of the cloud network
 */
public class CloudNetworkStateManagement {

    private CloudNetworkAgent cloudNetworkAgent;

    public CloudNetworkStateManagement(CloudNetworkAgent cloudNetworkAgent) {
        this.cloudNetworkAgent = cloudNetworkAgent;
    }

    /**
     * Method calculates the power in use at the given moment
     *
     * @return current power in use
     */
    public int getCurrentPowerInUse() {
        return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
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
        return cloudNetworkAgent.getNetworkJobs().keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
    }
}
