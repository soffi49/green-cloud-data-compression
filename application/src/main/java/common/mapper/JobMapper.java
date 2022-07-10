package common.mapper;

import domain.job.*;

import java.time.OffsetDateTime;

/**
 * Class provides set of methods mapping job classes
 */
public class JobMapper {

    /**
     * @param job job to be mapped to power job
     * @return PowerJob
     */
    public static PowerJob mapJobToPowerJob(final Job job) {
        return ImmutablePowerJob.builder()
                .jobId(job.getJobId())
                .power(job.getPower())
                .startTime(job.getStartTime())
                .endTime(job.getEndTime())
                .build();
    }

    /**
     * @param powerJob power job to be mapped to job
     * @return PowerJob
     */
    public static PowerJob mapToPowerJob(final PowerJob powerJob, final OffsetDateTime startTime) {
        return ImmutablePowerJob.builder()
                .jobId(powerJob.getJobId())
                .power(powerJob.getPower())
                .startTime(startTime)
                .endTime(powerJob.getEndTime())
                .build();
    }

    /**
     * @param job job to be mapped to job
     * @return Job
     */
    public static Job mapToJob(final Job job, final OffsetDateTime startTime) {
        return ImmutableJob.builder()
                .clientIdentifier(job.getClientIdentifier())
                .jobId(job.getJobId())
                .power(job.getPower())
                .startTime(startTime)
                .endTime(job.getEndTime())
                .build();
    }

    /**
     * @param powerJob PowerJob object
     * @return JobInstanceIdentifier
     */
    public static JobInstanceIdentifier mapToJobInstanceId(final PowerJob powerJob) {
        return ImmutableJobInstanceIdentifier.builder().jobId(powerJob.getJobId()).startTime(powerJob.getStartTime()).build();
    }

    /**
     * @param job       job to map
     * @param startTime power shortage start time
     * @return PowerShortageJob
     */
    public static PowerShortageJob mapToPowerShortageJob(final Job job, final OffsetDateTime startTime) {
        return ImmutablePowerShortageJob.builder().jobInstanceId(mapToJobInstanceId(job)).powerShortageStart(startTime).build();
    }

    /**
     * @param job Job object
     * @return JobInstanceIdentifier
     */
    public static JobInstanceIdentifier mapToJobInstanceId(final Job job) {
        return ImmutableJobInstanceIdentifier.builder().jobId(job.getJobId()).startTime(job.getStartTime()).build();
    }
}
