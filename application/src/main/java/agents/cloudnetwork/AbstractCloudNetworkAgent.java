package agents.cloudnetwork;

import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCloudNetworkAgent extends Agent{

    protected List<AID> serverAgentList;
    protected List<Job> currentJobs;
    protected List<Job> futureJobs;
    protected AID chosenServer;
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

    public List<AID> getServerAgentList() {
        return serverAgentList;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public void setMessagesSentCount(int messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
    }

    public AID getChosenServer() {
        return chosenServer;
    }

    public void setChosenServer(AID chosenServer) {
        this.chosenServer = chosenServer;
    }

    public void setServerAgentList(List<AID> serverAgentList) {
        this.serverAgentList = serverAgentList;
    }
}
