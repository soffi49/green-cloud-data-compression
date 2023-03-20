package com.greencloud.application.utils;

import static com.greencloud.application.utils.TimeUtils.alignStartTimeToCurrentTime;
import static com.greencloud.application.utils.TimeUtils.convertToRealTime;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.constants.CommonConstants.DATA_NOT_AVAILABLE_INDICATOR;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.ACCEPTED_JOB_STATUSES;
import static com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum.RUNNING_JOB_STATUSES;
import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.job.ServerJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.AID;

/**
 * Class defines set of utilities used to handle jobs
 */
public class JobUtils {

	public static final Long MAX_ERROR_IN_JOB_FINISH = 1000L;

	/**
	 * Method retrieves the job by the job id from job map
	 *
	 * @param jobId  job identifier
	 * @param jobMap map to traverse
	 * @return job
	 */
	@Nullable
	public static <T extends PowerJob> T getJobById(final String jobId, final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.keySet().stream()
				.filter(job -> job.getJobId().equals(jobId))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and start time from job map
	 *
	 * @param jobId     job identifier
	 * @param startTime job start time
	 * @param jobMap    map to traverse
	 * @return job
	 */
	@Nullable
	public static <T extends PowerJob> T getJobByIdAndStartDate(final String jobId, final Instant startTime,
			final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.keySet().stream()
				.filter(job -> job.getJobId().equals(jobId) && job.getStartTime().equals(startTime))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and start time from job map
	 *
	 * @param jobInstanceId unique identifier of the job instance
	 * @param jobMap        map to traverse
	 * @return job
	 */
	@Nullable
	public static <T extends PowerJob> T getJobByIdAndStartDate(final JobInstanceIdentifier jobInstanceId,
			final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.keySet().stream()
				.filter(job -> job.getJobId().equals(jobInstanceId.getJobId())
						&& job.getStartTime().equals(jobInstanceId.getStartTime()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and end time from job map
	 *
	 * @param jobId   job identifier
	 * @param endTime job end time
	 * @param jobMap  map to traverse
	 * @return job
	 */
	@Nullable
	public static <T extends PowerJob> T getJobByIdAndEndDate(final String jobId, final Instant endTime,
			final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.keySet().stream()
				.filter(job -> job.getJobId().equals(jobId) && job.getEndTime().equals(endTime))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the server job by the job id, start time and corresponding server from job map
	 *
	 * @param jobInstanceId unique identifier of the job instance
	 * @param server        server of interest
	 * @param jobMap        map to traverse
	 * @return ServerJob
	 */
	@Nullable
	public static ServerJob getJobByIdAndStartDateAndServer(final JobInstanceIdentifier jobInstanceId, final AID server,
			final Map<ServerJob, JobExecutionStatusEnum> jobMap) {
		return jobMap.keySet().stream()
				.filter(job -> job.getJobId().equals(jobInstanceId.getJobId())
						&& job.getStartTime().equals(jobInstanceId.getStartTime())
						&& job.getServer().equals(server))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method returns the instance of the job for current time
	 *
	 * @param jobId  unique job identifier
	 * @param jobMap map to traverse
	 * @return pair of job and current status
	 */
	@Nullable
	public static <T extends PowerJob> Map.Entry<T, JobExecutionStatusEnum> getCurrentJobInstance(final String jobId,
			final Map<T, JobExecutionStatusEnum> jobMap) {
		final Instant currentTime = getCurrentTime();
		return jobMap.entrySet().stream().filter(jobEntry -> {
			final T job = jobEntry.getKey();
			return job.getJobId().equals(jobId) && (
					(job.getStartTime().isBefore(currentTime) && job.getEndTime().isAfter(currentTime))
							|| job.getEndTime().equals(currentTime));
		}).findFirst().orElse(null);
	}

	/**
	 * Method returns number of jobs which execution status is contained in the given set
	 *
	 * @param jobMap   map of jobs to count
	 * @param statuses set of job statuses of interest
	 * @return integer being the number of jobs on hold
	 */
	public static <T extends PowerJob> int getJobCount(final Map<T, JobExecutionStatusEnum> jobMap,
			final Set<JobExecutionStatusEnum> statuses) {
		return jobMap.entrySet().stream()
				.filter(job -> statuses.contains(job.getValue()))
				.map(Map.Entry::getKey)
				.map(PowerJob::getJobId)
				.collect(toSet())
				.size();
	}

	/**
	 * Method returns number of currently started jobs
	 *
	 * @return integer being the number of currently started jobs
	 */
	public static <T extends PowerJob> int getJobCount(final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.entrySet().stream()
				.filter(job -> isJobStarted(job.getValue()))
				.map(Map.Entry::getKey)
				.map(PowerJob::getJobId)
				.collect(toSet())
				.size();
	}

	/**
	 * Method returns name of the original job for a given job part
	 *
	 * @param jobPart job part
	 * @return original job name
	 */
	public static <T extends PowerJob> String getJobName(final T jobPart) {
		return jobPart.getJobId().split("#")[0];
	}

	/**
	 * Method verifies if the given job has started
	 *
	 * @param job    job of interest
	 * @param jobMap map to traverse
	 * @return boolean indicating if a given job has started
	 */
	public static <T extends PowerJob> boolean isJobStarted(final T job, final Map<T, JobExecutionStatusEnum> jobMap) {
		return RUNNING_JOB_STATUSES.contains(jobMap.get(job));
	}

	/**
	 * Method verifies if the given job has started
	 *
	 * @param jobStatus current job status
	 * @return boolean indicating if a given job has started
	 */
	public static boolean isJobStarted(final JobExecutionStatusEnum jobStatus) {
		return RUNNING_JOB_STATUSES.contains(jobStatus);
	}

	/**
	 * Method verifies if there is only 1 instance of the given job
	 *
	 * @param jobId  unique job identifier
	 * @param jobMap map to traverse
	 * @return boolean
	 */
	public static <T extends PowerJob> boolean isJobUnique(final String jobId,
			final Map<T, JobExecutionStatusEnum> jobMap) {
		return jobMap.keySet().stream().filter(job -> job.getJobId().equals(jobId)).toList().size() == 1;
	}

	/**
	 * Method calculates expected job end time taking into account possible time error
	 *
	 * @param job job of interest
	 * @return date of expected job end time
	 */
	public static Date calculateExpectedJobEndTime(final PowerJob job) {
		final Instant endDate = alignStartTimeToCurrentTime(job.getEndTime());
		return Date.from(endDate.plus(MAX_ERROR_IN_JOB_FINISH, ChronoUnit.MILLIS));
	}

	/**
	 * Method retrieves list of time instances based on jobs' time-frames.
	 * It includes also job that is not in the processed job map.
	 *
	 * @param additionalJob - job that is to be included apart from jobs in the map
	 * @param jobMap        map of the jobs
	 * @return list of time instances
	 */
	public static <T extends PowerJob> List<Instant> getTimetableOfJobs(final T additionalJob,
			final Map<T, JobExecutionStatusEnum> jobMap) {
		var validJobs = jobMap.entrySet().stream()
				.filter(entry -> ACCEPTED_JOB_STATUSES.contains(entry.getValue()))
				.map(Map.Entry::getKey)
				.toList();

		return Stream.concat(
						Stream.of(
								convertToRealTime(additionalJob.getStartTime()),
								convertToRealTime(additionalJob.getEndTime())),
						Stream.concat(
								validJobs.stream().map(job -> convertToRealTime(job.getStartTime())),
								validJobs.stream().map(job -> convertToRealTime(job.getEndTime()))))
				.distinct()
				.toList();
	}

	/**
	 * Function computes ratio of succeeded jobs based on number of accepted and failed jobs
	 *
	 * @param acceptedJobs number of accepted jobs
	 * @param failedJobs   number of failed jobs
	 * @return double job success ratio or -1 if data is not available
	 */
	public static double getJobSuccessRatio(final long acceptedJobs, final long failedJobs) {
		return acceptedJobs == 0 ? DATA_NOT_AVAILABLE_INDICATOR : 1 - ((double) failedJobs / acceptedJobs);
	}
}
