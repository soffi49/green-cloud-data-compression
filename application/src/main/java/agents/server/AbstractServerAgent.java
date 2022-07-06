package agents.server;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateServerState;
import static java.time.temporal.ChronoUnit.HOURS;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;

import agents.AbstractAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.GreenSourceData;
import domain.job.Job;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Abstract agent class storing data of the Server Agent
 */
public abstract class AbstractServerAgent extends AbstractAgent {

    protected double pricePerHour;
    protected int maximumCapacity;
    protected transient ConcurrentMap<Job, JobStatusEnum> serverJobs;
    protected transient ConcurrentMap<String, AID> greenSourceForJobMap;
    protected List<AID> ownedGreenSources;
    protected AID ownerCloudNetworkAgent;

    AbstractServerAgent() {
        super.setup();

        serverJobs = new ConcurrentHashMap<>();
        ownedGreenSources = new ArrayList<>();
        greenSourceForJobMap = new ConcurrentHashMap<>();
    }

    /**
     * Abstract Server Agent constructor.
     *
     * @param pricePerHour           price for 1-hour server service
     * @param maximumCapacity        maximum available server power capacity
     * @param serverJobs             list of jobs together with their status that are being processed
     *                               by the server
     * @param greenSourceForJobMap   map storing jobs and corresponding job's executor addresses
     * @param ownedGreenSources      list of addresses of owned green sources
     * @param ownerCloudNetworkAgent address of the owner cloud network agent
     */
    AbstractServerAgent(double pricePerHour,
                        int maximumCapacity,
                        ConcurrentMap<Job, JobStatusEnum> serverJobs,
                        ConcurrentMap<String, AID> greenSourceForJobMap,
                        List<AID> ownedGreenSources,
                        AID ownerCloudNetworkAgent) {
        this.pricePerHour = pricePerHour;
        this.maximumCapacity = maximumCapacity;
        this.serverJobs = serverJobs;
        this.greenSourceForJobMap = greenSourceForJobMap;
        this.ownedGreenSources = ownedGreenSources;
        this.ownerCloudNetworkAgent = ownerCloudNetworkAgent;
    }

    /**
     * Method calculates the power in use at the given moment for the server
     *
     * @return current power in use
     */
    public int getCurrentPowerInUse() {
        return serverJobs.entrySet().stream()
                .filter(job -> job.getValue().equals(JobStatusEnum.IN_PROGRESS))
                .mapToInt(job -> job.getKey().getPower()).sum();
    }

    /**
     * Method retrieves if the given server is currently active or idle
     *
     * @return green source state
     */
    public boolean getIsActiveState() {
        return !serverJobs.entrySet().stream().filter(entry -> entry.getValue().equals(JobStatusEnum.IN_PROGRESS)).toList().isEmpty();
    }

    /**
     * Method retrieves the number of jobs that are executed by the server
     *
     * @return jobs count
     */
    public int getJobCount() {
        return serverJobs.keySet().stream().map(Job::getJobId).collect(Collectors.toSet()).size();
    }

    /**
     * Method computes the available power for given time frame
     *
     * @param startDate starting date
     * @param endDate   end date
     * @return available power
     */
    public int getAvailableCapacity(final OffsetDateTime startDate,
                                    final OffsetDateTime endDate) {
        final int powerInUser =
                serverJobs.keySet().stream()
                        .filter(job -> job.getStartTime().isBefore(endDate) && job.getEndTime().isAfter(startDate))
                        .mapToInt(Job::getPower).sum();
        return maximumCapacity - powerInUser;
    }

    /**
     * Method performs default behaviour when the job is finished
     *
     * @param jobToFinish job to be finished
     * @param informCNA   flag indicating whether cloud network should be informed about the job finish
     */
    public void finishJobExecution(final Job jobToFinish, final boolean informCNA) {
        final List<AID> receivers = informCNA ? List.of(greenSourceForJobMap.get(jobToFinish.getJobId()), ownerCloudNetworkAgent) :
                Collections.singletonList(greenSourceForJobMap.get(jobToFinish.getJobId()));
        final ACLMessage finishJobMessage = prepareFinishMessage(jobToFinish.getJobId(), jobToFinish.getStartTime(), receivers);
        serverJobs.remove(jobToFinish);
        greenSourceForJobMap.remove(jobToFinish.getJobId());
        updateServerState((ServerAgent) this);
        displayMessageArrow(this, receivers);
        this.send(finishJobMessage);
    }

    /**
     * Method chooses the green source for job execution
     *
     * @param greenSourceOffers offers from green sources
     * @return chosen offer
     */
    public ACLMessage chooseGreenSourceToExecuteJob(final List<ACLMessage> greenSourceOffers) {
        final Comparator<ACLMessage> compareGreenSources =
                Comparator.comparingDouble(greenSource -> {
                    try {
                        return getMapper().readValue(greenSource.getContent(), GreenSourceData.class).getAvailablePowerInTime();
                    } catch (final JsonProcessingException e) {
                        return Double.MAX_VALUE;
                    }
                });
        return greenSourceOffers.stream().min(compareGreenSources).orElseThrow();
    }

    /**
     * Method calculates the price for executing the job by given green source and server
     *
     * @param greenSourceData green source executing the job
     * @return full price
     */
    public double calculateServicePrice(final GreenSourceData greenSourceData) {
        var job = getJobById(greenSourceData.getJobId());
        var powerCost = job.getPower() * greenSourceData.getPricePerPowerUnit();
        var computingCost =
                HOURS.between(job.getEndTime(), job.getStartTime()) * getPricePerHour();
        return powerCost + computingCost;
    }

    /**
     * Method retrieves the job by the job id and star time from job map
     *
     * @param jobId     job identifier
     * @param startTime job start time
     * @return job
     */
    public Job getJobByIdAndStartDate(final String jobId, final OffsetDateTime startTime) {
        return serverJobs.keySet().stream()
                .filter(job -> job.getJobId().equals(jobId) && job.getStartTime().isEqual(startTime)).findFirst().orElse(null);
    }

    /**
     * Method retrieves the job by the job id and end time from job map
     *
     * @param jobId   job identifier
     * @param endTime job end time
     * @return job
     */
    public Job getJobByIdAndEndDate(final String jobId, final OffsetDateTime endTime) {
        return serverJobs.keySet().stream()
                .filter(job -> job.getJobId().equals(jobId) && job.getEndTime().isEqual(endTime))
                .findFirst()
                .orElse(null);
    }

    public Job getJobById(final String jobId) {
        return serverJobs.keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
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

    public ConcurrentMap<Job, JobStatusEnum> getServerJobs() {
        return serverJobs;
    }

    public void setServerJobs(ConcurrentHashMap<Job, JobStatusEnum> serverJobs) {
        this.serverJobs = serverJobs;
    }

    public List<AID> getOwnedGreenSources() {
        return ownedGreenSources;
    }

    public void setOwnedGreenSources(List<AID> ownedGreenSources) {
        this.ownedGreenSources = ownedGreenSources;
    }

    public ConcurrentMap<String, AID> getGreenSourceForJobMap() {
        return greenSourceForJobMap;
    }

    public void setGreenSourceForJobMap(ConcurrentHashMap<String, AID> greenSourceForJobMap) {
        this.greenSourceForJobMap = greenSourceForJobMap;
    }
}
