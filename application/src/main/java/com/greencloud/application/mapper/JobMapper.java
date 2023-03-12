package com.greencloud.application.mapper;

import static com.greencloud.application.utils.TimeUtils.convertToRealTime;

import java.time.Instant;

import com.greencloud.application.domain.job.ImmutableJobInstanceIdentifier;
import com.greencloud.application.domain.job.ImmutableJobPowerShortageTransfer;
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
 * Class provides set of methods mapping job classes
 */
public class JobMapper {

	/**
	 * @param job job to be mapped to power job
	 * @return PowerJob
	 */
	public static PowerJob mapJobToPowerJob(final ClientJob job) {
		return ImmutablePowerJob.builder()
				.jobId(job.getJobId())
				.power(job.getPower())
				.startTime(job.getStartTime())
				.endTime(job.getEndTime())
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param job server job to be mapped to power job
	 * @return PowerJob
	 */
	public static PowerJob mapServerJobToPowerJob(final ServerJob job) {
		return ImmutablePowerJob.builder()
				.jobId(job.getJobId())
				.power(job.getPower())
				.startTime(job.getStartTime())
				.endTime(job.getEndTime())
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param powerJob  power job to be mapped to job
	 * @param startTime new start time
	 * @return PowerJob
	 */
	public static PowerJob mapToPowerJob(final ClientJob powerJob, final Instant startTime) {
		return ImmutablePowerJob.builder()
				.jobId(powerJob.getJobId())
				.power(powerJob.getPower())
				.startTime(startTime)
				.endTime(powerJob.getEndTime())
				.deadline(powerJob.getDeadline())
				.build();
	}

	/**
	 * @param serverJob server job to be mapped to job
	 * @return ServerJob
	 */
	public static ServerJob mapToServerJobRealTime(final ServerJob serverJob) {
		return ImmutableServerJob.builder()
				.server(serverJob.getServer())
				.jobId(serverJob.getJobId())
				.power(serverJob.getPower())
				.startTime(convertToRealTime(serverJob.getStartTime()))
				.endTime(convertToRealTime(serverJob.getEndTime()))
				.deadline(convertToRealTime(serverJob.getDeadline()))
				.build();
	}

	/**
	 * @param job client job to be mapped to job
	 * @return ClientJob
	 */
	public static ClientJob mapToClientJobRealTime(final ClientJob job) {
		return ImmutableClientJob.builder()
				.clientIdentifier(job.getClientIdentifier())
				.jobId(job.getJobId())
				.power(job.getPower())
				.startTime(convertToRealTime(job.getStartTime()))
				.endTime(convertToRealTime(job.getEndTime()))
				.deadline(convertToRealTime(job.getDeadline()))
				.build();
	}

	/**
	 * @param job       job to be mapped to job
	 * @param startTime new job start time
	 * @return Job
	 */
	public static ClientJob mapToJobNewStartTime(final ClientJob job, final Instant startTime) {
		return ImmutableClientJob.builder()
				.clientIdentifier(job.getClientIdentifier())
				.jobId(job.getJobId())
				.power(job.getPower())
				.startTime(startTime)
				.endTime(job.getEndTime())
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param job job to be mapped to job
	 * @return Job
	 */
	public static ClientJob mapToJobNewEndTime(final ClientJob job, final Instant endTime) {
		return ImmutableClientJob.builder()
				.clientIdentifier(job.getClientIdentifier())
				.jobId(job.getJobId())
				.power(job.getPower())
				.startTime(job.getStartTime())
				.endTime(endTime)
				.deadline(job.getDeadline())
				.build();
	}

	/**
	 * @param serverJob server job to be mapped to job
	 * @param startTime new power job start time
	 * @return ServerJob
	 */
	public static ServerJob mapToJobNewStartTime(final ServerJob serverJob, final Instant startTime) {
		return ImmutableServerJob.builder()
				.server(serverJob.getServer())
				.jobId(serverJob.getJobId())
				.power(serverJob.getPower())
				.startTime(startTime)
				.endTime(serverJob.getEndTime())
				.deadline(serverJob.getDeadline())
				.build();
	}

	/**
	 * @param serverJob server job to be mapped to job
	 * @param endTime   new power job end time
	 * @return ServerJob
	 */
	public static ServerJob mapToJobNewEndTime(final ServerJob serverJob, final Instant endTime) {
		return ImmutableServerJob.builder()
				.server(serverJob.getServer())
				.jobId(serverJob.getJobId())
				.power(serverJob.getPower())
				.startTime(serverJob.getStartTime())
				.endTime(endTime)
				.deadline(serverJob.getDeadline())
				.build();
	}

	/**
	 * @param job       job to be mapped to job
	 * @param endTime   new job end time
	 * @param startTime new job start time
	 * @return PowerJob
	 */
	public static ClientJob mapToJobWithNewTime(final ClientJob job, final Instant startTime, final Instant endTime) {
		return ImmutableClientJob.builder()
				.jobId(job.getJobId())
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
		return ImmutableJobInstanceIdentifier.of(powerJob.getJobId(), powerJob.getStartTime());
	}

	/**
	 * @param job       job to map
	 * @param startTime power shortage start time
	 * @return PowerShortageJob
	 */
	public static JobPowerShortageTransfer mapToPowerShortageJob(final ClientJob job, final Instant startTime) {
		return ImmutableJobPowerShortageTransfer.of(mapToJobInstanceId(job), startTime);
	}

	/**
	 * @param jobInstanceId job identifier
	 * @param startTime     power shortage start time
	 * @return PowerShortageJob
	 */
	public static JobPowerShortageTransfer mapToPowerShortageJob(final JobInstanceIdentifier jobInstanceId,
			final Instant startTime) {
		return ImmutableJobPowerShortageTransfer.of(jobInstanceId, startTime);
	}

	/**
	 * @param job       power job to map
	 * @param startTime power shortage start time
	 * @return PowerShortageJob
	 */
	public static JobPowerShortageTransfer mapToPowerShortageJob(final PowerJob job, final Instant startTime) {
		return ImmutableJobPowerShortageTransfer.of(mapToJobInstanceId(job), startTime);
	}

	/**
	 * @param job Job object
	 * @return JobInstanceIdentifier
	 */
	public static JobInstanceIdentifier mapToJobInstanceId(final ClientJob job) {
		return ImmutableJobInstanceIdentifier.of(job.getJobId(), job.getStartTime());
	}

	/**
	 * @param jobInstanceId job instance
	 * @param startTime     job instance start time
	 * @return JobInstanceIdentifier
	 */
	public static JobInstanceIdentifier mapToJobInstanceId(final JobInstanceIdentifier jobInstanceId,
			final Instant startTime) {
		return ImmutableJobInstanceIdentifier.of(jobInstanceId.getJobId(), startTime);
	}

	/**
	 * @param jobId     job identifier
	 * @param startTime job instance start time
	 * @return JobInstanceIdentifier
	 */
	public static JobInstanceIdentifier mapToJobInstanceId(final String jobId, final Instant startTime) {
		return ImmutableJobInstanceIdentifier.of(jobId, startTime);
	}
}
