package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerData implements Serializable {

    @JsonIgnore
    private List<Job> currentJobs;
    @JsonIgnore
    private List<Job> futureJobs;
    @JsonProperty("inUsePower")
    private int inUsePower;
    @JsonProperty("availablePower")
    private int availablePower;
    @JsonProperty("jobsCount")
    private int jobsCount;

    public ServerData() {
        inUsePower = 0;
        availablePower = 0;
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

    public int getAvailablePower() {
        return availablePower;
    }

    public void setAvailablePower(int availablePower) {
        this.availablePower = availablePower;
    }
}
