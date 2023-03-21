package com.greencloud.application.mapper;

import static com.greencloud.application.utils.TimeUtils.convertToRealTime;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.UUID;

import org.apache.commons.math3.util.Pair;

import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.ImmutableJobPowerShortageTransfer;
import com.greencloud.application.domain.job.JobDivided;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobPowerShortageTransfer;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.greencloud.commons.domain.job.ImmutablePowerJob;
import com.greencloud.commons.domain.job.ImmutableServerJob;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.ServerJob;

import jade.core.AID;

/**
 * Class provides set of methods mapping job object classes
 */
public class JobMapper {

	/**
	 * @param job job to be mapped to power job
	 * @return PowerJob
	 */
	public static PowerJob mapJobToPowerJob(final ClientJob job) {
		return ImmutablePowerJob.builder()
				.jobId(job.getJobId())
				.jobInstanceId(job.getJobInstanceId())
				.power(job.getPower())
				.startTime(job.getStartTime())
				.endTime(job.getEndTime())
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param serverJob server job to be mapped to job with time frames referencing real time
	 * @return ServerJob
	 */
	public static ServerJob mapToServerJobRealTime(final ServerJob serverJob) {
		return ImmutableServerJob.builder()
				.server(serverJob.getServer())
				.jobId(serverJob.getJobId())
				.jobInstanceId(serverJob.getJobInstanceId())
				.power(serverJob.getPower())
				.startTime(convertToRealTime(serverJob.getStartTime()))
				.endTime(convertToRealTime(serverJob.getEndTime()))
				.deadline(convertToRealTime(serverJob.getDeadline()))
				.build();
	}

	/**
	 * @param job client job to be mapped to job with time frames referencing real time
	 * @return ClientJob
	 */
	public static ClientJob mapToClientJobRealTime(final ClientJob job) {
		return ImmutableClientJob.builder()
				.clientIdentifier(job.getClientIdentifier())
				.jobId(job.getJobId())
				.jobInstanceId(job.getJobInstanceId())
				.power(job.getPower())
				.startTime(convertToRealTime(job.getStartTime()))
				.endTime(convertToRealTime(job.getEndTime()))
				.deadline(convertToRealTime(job.getDeadline()))
				.build();
	}

	/**
	 * @param job       job to be mapped to job
	 * @param startTime new job start time
	 * @return ClientJob
	 */
	public static ClientJob mapToJobNewStartTime(final ClientJob job, final Instant startTime) {
		return ImmutableClientJob.builder()
				.clientIdentifier(job.getClientIdentifier())
				.jobId(job.getJobId())
				.jobInstanceId(job.getJobInstanceId())
				.power(job.getPower())
				.startTime(startTime)
				.endTime(job.getEndTime())
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param job         job to be mapped
	 * @param jobInstance new job instance data
	 * @return job extending PowerJob
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PowerJob> T mapToJobStartTimeAndInstanceId(final T job,
			final JobInstanceIdentifier jobInstance) {
		return job instanceof ClientJob clientJob ?
				(T) ImmutableClientJob.builder()
						.clientIdentifier(clientJob.getClientIdentifier())
						.jobId(clientJob.getJobId())
						.jobInstanceId(jobInstance.getJobInstanceId())
						.power(clientJob.getPower())
						.startTime(jobInstance.getStartTime())
						.endTime(clientJob.getEndTime())
						.deadline(clientJob.getDeadline())
						.build() :
				(T) ImmutableServerJob.builder()
						.server(((ServerJob) job).getServer())
						.jobId(job.getJobId())
						.jobInstanceId(jobInstance.getJobInstanceId())
						.power(job.getPower())
						.startTime(jobInstance.getStartTime())
						.endTime(job.getEndTime())
						.deadline(job.getDeadline())
						.build();
	}

	/**
	 * @param job           job to be mapped
	 * @param jobInstanceId job instance identifier
	 * @param endTime       new end time
	 * @return job extending PowerJob
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PowerJob> T mapToJobEndTimeAndInstanceId(final T job,
			final String jobInstanceId, final Instant endTime) {
		return job instanceof ClientJob clientJob ?
				(T) ImmutableClientJob.builder()
						.clientIdentifier(clientJob.getClientIdentifier())
						.jobId(clientJob.getJobId())
						.jobInstanceId(jobInstanceId)
						.power(clientJob.getPower())
						.startTime(clientJob.getStartTime())
						.endTime(endTime)
						.deadline(clientJob.getDeadline())
						.build() :
				(T) ImmutableServerJob.builder()
						.server(((ServerJob) job).getServer())
						.jobId(job.getJobId())
						.jobInstanceId(jobInstanceId)
						.power(job.getPower())
						.startTime(job.getStartTime())
						.endTime(endTime)
						.deadline(job.getDeadline())
						.build();
	}

	/**
	 * @param job       job extending PowerJob that is to be mapped to job
	 * @param startTime new job start time
	 * @return job extending PowerJob
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PowerJob> T mapToNewJobInstanceStartTime(final T job, final Instant startTime) {
		return job instanceof ClientJob clientJob ?
				(T) ImmutableClientJob.builder()
						.clientIdentifier(clientJob.getClientIdentifier())
						.jobId(clientJob.getJobId())
						.power(clientJob.getPower())
						.startTime(startTime)
						.endTime(clientJob.getEndTime())
						.deadline(clientJob.getDeadline())
						.build() :
				(T) ImmutableServerJob.builder()
						.server(((ServerJob) job).getServer())
						.jobId(job.getJobId())
						.power(job.getPower())
						.startTime(startTime)
						.endTime(job.getEndTime())
						.deadline(job.getDeadline())
						.build();
	}

	/**
	 * @param job     job extending PowerJob to be mapped to job
	 * @param endTime new job end time
	 * @return job extending PowerJob
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PowerJob> T mapToNewJobInstanceEndTime(final T job, final Instant endTime) {
		return job instanceof ClientJob clientJob ?
				(T) ImmutableClientJob.builder()
						.clientIdentifier(clientJob.getClientIdentifier())
						.jobId(clientJob.getJobId())
						.power(clientJob.getPower())
						.startTime(clientJob.getStartTime())
						.endTime(endTime)
						.deadline(clientJob.getDeadline())
						.build() :
				(T) ImmutableServerJob.builder()
						.server(((ServerJob) job).getServer())
						.jobId(job.getJobId())
						.power(job.getPower())
						.startTime(job.getStartTime())
						.endTime(endTime)
						.deadline(job.getDeadline())
						.build();
	}

	/**
	 * @param job       job to be mapped to job
	 * @param endTime   new job end time
	 * @param startTime new job start time
	 * @return ClientJob
	 */
	public static ClientJob mapToJobWithNewTime(final ClientJob job, final Instant startTime, final Instant endTime) {
		return ImmutableClientJob.builder()
				.jobId(job.getJobId())
				.jobInstanceId(job.getJobInstanceId())
				.clientIdentifier(job.getClientIdentifier())
				.power(job.getPower())
				.startTime(startTime)
				.endTime(endTime)
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param powerJob power job
	 * @param server   server that sent given job
	 * @return ServerJob
	 */
	public static ServerJob mapToServerJob(final PowerJob powerJob, final AID server) {
		return ImmutableServerJob.builder()
				.server(server)
				.jobId(powerJob.getJobId())
				.jobInstanceId(powerJob.getJobInstanceId())
				.power(powerJob.getPower())
				.startTime(powerJob.getStartTime())
				.endTime(powerJob.getEndTime())
				.deadline(powerJob.getDeadline())
				.build();
	}

	/**
	 * @param powerJob PowerJob object
	 * @return JobInstanceIdentifier
	 */
	public static JobInstanceIdentifier mapToJobInstanceId(final PowerJob powerJob) {
		return new ImmutableJobInstanceIdentifier(powerJob.getJobId(), powerJob.getJobInstanceId(),
				powerJob.getStartTime());
	}

	/**
	 * @param job ClientJob object
	 * @return JobInstanceIdentifier
	 */
	public static JobInstanceIdentifier mapToJobInstanceId(final ClientJob job) {
		return new ImmutableJobInstanceIdentifier(job.getJobId(), job.getJobInstanceId(), job.getStartTime());
	}

	/**
	 * @param jobInstanceId job instance
	 * @param startTime     job instance start time
	 * @return JobInstanceIdentifier
	 */
	public static JobInstanceIdentifier mapToJobInstanceId(final JobInstanceIdentifier jobInstanceId,
			final Instant startTime) {
		return new ImmutableJobInstanceIdentifier(jobInstanceId.getJobId(), jobInstanceId.getJobInstanceId(),
				startTime);
	}

	/**
	 * @param jobInstance job identifier of job that is to be transferred
	 * @param startTime   power shortage start time
	 * @return JobPowerShortageTransfer
	 */
	public static JobPowerShortageTransfer mapToPowerShortageJob(final JobInstanceIdentifier jobInstance,
			final Instant startTime) {
		return new ImmutableJobPowerShortageTransfer(null, null, jobInstance, startTime);
	}

	/**
	 * @param originalJobInstanceId unique identifier of original job
	 * @param jobInstances          pair of job instances
	 * @param startTime             power shortage start time
	 * @return JobPowerShortageTransfer
	 */
	public static <T extends PowerJob> JobPowerShortageTransfer mapToPowerShortageJob(
			final String originalJobInstanceId, final JobDivided<T> jobInstances, final Instant startTime) {
		final Pair<JobInstanceIdentifier, JobInstanceIdentifier> mappedInstances = isNull(
				jobInstances.getFirstInstance()) ?
				new Pair<>(null, mapToJobInstanceId(jobInstances.getSecondInstance())) :
				new Pair<>(mapToJobInstanceId(jobInstances.getFirstInstance()),
						mapToJobInstanceId(jobInstances.getSecondInstance()));
		return new ImmutableJobPowerShortageTransfer(originalJobInstanceId, mappedInstances.getFirst(),
				mappedInstances.getSecond(), startTime);
	}

	/**
	 * @param job    job for splitting
	 * @param partNo number of job part
	 * @return job part
	 */
	public static ClientJob mapToJobPart(final ClientJob job, final int partNo, final int splitFactor) {
		return ImmutableClientJob.builder()
				.from(job)
				.jobInstanceId(UUID.randomUUID().toString())
				.jobId(format("%s#part%d", job.getJobId(), partNo))
				.power(job.getPower() / splitFactor)
				.build();
	}
}
