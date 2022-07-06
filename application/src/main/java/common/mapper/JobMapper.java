package common.mapper;

import domain.job.ImmutablePowerJob;
import domain.job.Job;
import domain.job.PowerJob;

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
     * @return Job
     */
    public static PowerJob mapPowerJobToPowerJob(final PowerJob powerJob, final OffsetDateTime startTime) {
        return ImmutablePowerJob.builder()
                .jobId(powerJob.getJobId())
                .power(powerJob.getPower())
                .startTime(startTime)
                .endTime(powerJob.getEndTime())
                .build();
    }
}
