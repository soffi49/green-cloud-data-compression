package agents.server.domain;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateServerState;
import static common.TimeUtils.getCurrentTime;
import static java.time.temporal.ChronoUnit.HOURS;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;

import agents.server.ServerAgent;
import agents.server.behaviour.FinishJobExecution;
import agents.server.behaviour.StartJobExecution;
import common.mapper.JobMapper;
import domain.GreenSourceData;
import domain.job.ImmutableJob;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Set of utilities used to manage the internal state of the server agent
 */
public class ServerStateManagement {

    private final ServerAgent serverAgent;

    public ServerStateManagement(ServerAgent serverAgent) {
        this.serverAgent = serverAgent;
    }

    /**
     * Method calculates the power in use at the given moment for the server
     *
     * @return current power in use
     */
    public int getCurrentPowerInUseForGreenSource() {
        return serverAgent.getServerJobs().entrySet().stream()
                .filter(job -> job.getValue().equals(JobStatusEnum.IN_PROGRESS))
                .mapToInt(job -> job.getKey().getPower()).sum();
    }

    /**
     * Method retrieves if the given server is currently active or idle
     *
     * @return green source state
     */
    public boolean getIsActiveState() {
        return getCurrentPowerInUseForGreenSource() > 0;
    }

    /**
     * Method retrieves the number of jobs that are executed by the server
     *
     * @return jobs count
     */
    public int getJobCount() {
        return serverAgent.getServerJobs().keySet().stream().map(Job::getJobId).collect(Collectors.toSet()).size();
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
        final int powerInUser = getUniqueJobsForTimeStamp(startDate, endDate).stream().mapToInt(Job::getPower).sum();
        return serverAgent.getMaximumCapacity() - powerInUser;
    }

    /**
     * Method performs default behaviour when the job is finished
     *
     * @param jobToFinish job to be finished
     * @param informCNA   flag indicating whether cloud network should be informed about the job finish
     */
    public void finishJobExecution(final Job jobToFinish, final boolean informCNA) {
        final List<AID> receivers = informCNA ? List.of(serverAgent.getGreenSourceForJobMap().get(jobToFinish.getJobId()), serverAgent.getOwnerCloudNetworkAgent()) :
                Collections.singletonList(serverAgent.getGreenSourceForJobMap().get(jobToFinish.getJobId()));
        final ACLMessage finishJobMessage = prepareFinishMessage(jobToFinish.getJobId(), jobToFinish.getStartTime(), receivers);
        serverAgent.getServerJobs().remove(jobToFinish);
        if(informCNA) {
            serverAgent.getGreenSourceForJobMap().remove(jobToFinish.getJobId());
        }
        updateServerState(serverAgent);
        displayMessageArrow(serverAgent, receivers);
        serverAgent.send(finishJobMessage);
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
                HOURS.between(job.getEndTime(), job.getStartTime()) * serverAgent.getPricePerHour();
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
        return serverAgent.getServerJobs().keySet().stream()
                .filter(job -> job.getJobId().equals(jobId) && job.getStartTime().isEqual(startTime)).findFirst().orElse(null);
    }

    /**
     * Method retrieves the job by the job instance id
     *
     * @param jobInstanceId job instance identifier
     * @return job
     */
    public Job getJobByIdAndStartDate(final JobInstanceIdentifier jobInstanceId) {
        return serverAgent.getServerJobs().keySet().stream()
                .filter(job -> job.getJobId().equals(jobInstanceId.getJobId()) && job.getStartTime().isEqual(jobInstanceId.getStartTime()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Method retrieves the job by the job id and end time from job map
     *
     * @param jobId   job identifier
     * @param endTime job end time
     * @return job
     */
    public Job getJobByIdAndEndDate(final String jobId, final OffsetDateTime endTime) {
        return serverAgent.getServerJobs().keySet().stream()
                .filter(job -> job.getJobId().equals(jobId) && job.getEndTime().isEqual(endTime))
                .findFirst()
                .orElse(null);
    }

    /**
     * Method retrieves the job based on the given id
     *
     * @param jobId unique job identifier
     * @return Job
     */
    public Job getJobById(final String jobId) {
        return serverAgent.getServerJobs().keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
    }

    /**
     * Method creates new instances for given job which will be affected by the power shortage
     *
     * @param job                affected job
     * @param powerShortageStart time when power shortage starts
     */
    public void divideJobForPowerShortage(final Job job, final OffsetDateTime powerShortageStart) {
        if (powerShortageStart.isAfter(job.getStartTime())) {
            final Job onBackupEnergyInstance = ImmutableJob.builder()
                    .jobId(job.getJobId())
                    .clientIdentifier(job.getClientIdentifier())
                    .power(job.getPower())
                    .startTime(powerShortageStart)
                    .endTime(job.getEndTime())
                    .build();
            final Job finishedPowerJobInstance = ImmutableJob.builder()
                    .jobId(job.getJobId())
                    .clientIdentifier(job.getClientIdentifier())
                    .power(job.getPower())
                    .startTime(job.getStartTime())
                    .endTime(powerShortageStart)
                    .build();
            final JobStatusEnum currentJobStatus = serverAgent.getServerJobs().get(job);
            serverAgent.getServerJobs().remove(job);
            serverAgent.getServerJobs().put(onBackupEnergyInstance, JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY);
            serverAgent.getServerJobs().put(finishedPowerJobInstance, currentJobStatus);
            serverAgent.addBehaviour(FinishJobExecution.createFor(serverAgent, finishedPowerJobInstance, false));
            serverAgent.addBehaviour(FinishJobExecution.createFor(serverAgent, onBackupEnergyInstance, true));
            if(getCurrentTime().isBefore(finishedPowerJobInstance.getStartTime())) {
                serverAgent.addBehaviour(StartJobExecution.createFor(serverAgent, JobMapper.mapToJobInstanceId(finishedPowerJobInstance), true));
            }
        } else {
            serverAgent.getServerJobs().replace(job, JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY);
        }
    }

    private List<Job> getUniqueJobsForTimeStamp(final OffsetDateTime startDate,
                                                final OffsetDateTime endDate) {
        return serverAgent.getServerJobs().keySet().stream()
                .filter(job -> job.getStartTime().isBefore(endDate) && job.getEndTime().isAfter(startDate))
                .map(Job::getJobId)
                .collect(Collectors.toMap(jobId -> jobId, this::getJobById))
                .values().stream().toList();
    }
}
