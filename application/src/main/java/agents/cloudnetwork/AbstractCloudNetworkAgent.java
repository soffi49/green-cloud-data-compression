package agents.cloudnetwork;

import domain.job.Job;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCloudNetworkAgent extends Agent{

    private List<Job> currentJobs;
    private List<Job> futureJobs;
    private int inUsePower;
    private int jobsCount;

    @Override
    protected void setup() {
        super.setup();

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
