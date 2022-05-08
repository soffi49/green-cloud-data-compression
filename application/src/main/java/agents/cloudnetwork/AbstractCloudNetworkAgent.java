package agents.cloudnetwork;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.Job;
import jade.core.Agent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractCloudNetworkAgent extends Agent implements Serializable {

    @JsonIgnore
    private List<Job> currentJobs;
    @JsonIgnore
    private List<Job> futureJobs;
    @JsonProperty("inUsePower")
    private int inUsePower;
    @JsonProperty("jobsCount")
    private int jobsCount;

    protected AbstractCloudNetworkAgent() {
        inUsePower = 0;
        currentJobs = new ArrayList<>();
        futureJobs = new ArrayList<>();
        jobsCount = 0;
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

    public int getJobsCount() {
        return jobsCount;
    }

    public void setJobsCount(int jobsCount) {
        this.jobsCount = jobsCount;
    }
}
