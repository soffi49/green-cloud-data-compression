package agents.cloudnetwork;

import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractCloudNetworkAgent extends Agent{

    protected List<Job> currentJobs;
    protected List<Job> futureJobs;
    protected Map<Job, AID> serverForJobMap;
    protected int inUsePower;
    protected int jobsCount;
    protected int messagesSentCount;

    @Override
    protected void setup() {
        super.setup();

        inUsePower = 0;
        currentJobs = new ArrayList<>();
        futureJobs = new ArrayList<>();
        jobsCount = 0;
    }

    public Map<Job, AID> getServerForJobMap() {
        return serverForJobMap;
    }

    public void setServerForJobMap(Map<Job, AID> serverForJobMap) {
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

    public int getJobsCount() {
        return jobsCount;
    }

    public void setJobsCount(int jobsCount) {
        this.jobsCount = jobsCount;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public void setMessagesSentCount(int messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
    }
}
