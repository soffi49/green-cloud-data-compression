package com.greencloud.application.agents.client.domain;

import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.time.Instant;
import java.util.Map;

import com.greencloud.application.utils.domain.Timer;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.greencloud.commons.domain.job.enums.JobClientStatusEnum;

/**
 * Class containing data and method associated with state of the execution of the client's job
 */
public class ClientJobExecution {

	protected final Timer timer = new Timer();
	protected ClientJob job;
	protected Instant jobSimulatedStart;
	protected Instant jobSimulatedEnd;
	protected Instant jobSimulatedDeadline;
	protected JobClientStatusEnum jobStatus;
	protected Map<JobClientStatusEnum, Long> jobDurationMap;

	ClientJobExecution() {
		this.jobDurationMap = stream(JobClientStatusEnum.values()).collect(
				toMap(statusEnum -> statusEnum, statusEnum -> 0L));
		this.timer.startTimeMeasure(getCurrentTime());
	}

	/**
	 * Class constructor
	 *
	 * @param job                  job assigned for execution
	 * @param jobSimulatedStart    job execution start time (converted to simulation time)
	 * @param jobSimulatedEnd      job execution end time (converted to simulation time)
	 * @param jobSimulatedDeadline job execution deadline (converted to simulation time)
	 * @param jobStatus            current job status
	 */
	public ClientJobExecution(final ClientJob job, final Instant jobSimulatedStart, final Instant jobSimulatedEnd,
			final Instant jobSimulatedDeadline, final JobClientStatusEnum jobStatus) {
		this();
		this.job = job;
		this.jobSimulatedStart = jobSimulatedStart;
		this.jobSimulatedEnd = jobSimulatedEnd;
		this.jobSimulatedDeadline = jobSimulatedDeadline;
		this.jobStatus = jobStatus;
	}

	/**
	 * Class constructor
	 *
	 * @param clientAID identifier of the client
	 * @param start     job execution start time (converted to simulation time)
	 * @param end       job execution end time (converted to simulation time)
	 * @param deadline  job execution deadline (converted to simulation time)
	 * @param power     power needed for job execution
	 * @param jobId     job identifier
	 */
	public ClientJobExecution(final String clientAID, final Instant start, final Instant end,
			final Instant deadline, final int power, final String jobId) {
		this(ImmutableClientJob.of(jobId, start, end, deadline, power, clientAID), start, end, deadline, CREATED);
	}

	public ClientJob getJob() {
		return job;
	}

	public void setJob(ClientJob job) {
		this.job = job;
	}

	public Instant getJobSimulatedStart() {
		return jobSimulatedStart;
	}

	public void setJobSimulatedStart(Instant jobSimulatedStart) {
		this.jobSimulatedStart = jobSimulatedStart;
	}

	public Instant getJobSimulatedEnd() {
		return jobSimulatedEnd;
	}

	public void setJobSimulatedEnd(Instant jobSimulatedEnd) {
		this.jobSimulatedEnd = jobSimulatedEnd;
	}

	public Instant getJobSimulatedDeadline() {
		return jobSimulatedDeadline;
	}

	public JobClientStatusEnum getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(JobClientStatusEnum jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Map<JobClientStatusEnum, Long> getJobDurationMap() {
		return jobDurationMap;
	}

	public void setJobDurationMap(
			Map<JobClientStatusEnum, Long> jobDurationMap) {
		this.jobDurationMap = jobDurationMap;
	}

	public Timer getTimer() {
		return timer;
	}

	/**
	 * Method updates data in job status duration map (i.e. updates time during which the job was at the given status)
	 *
	 * @param newStatus new status of the job
	 * @param time      time when the job execution has changed the status
	 */
	public synchronized void updateJobStatusDuration(final JobClientStatusEnum newStatus, final Instant time) {
		final long elapsedTime = timer.stopTimeMeasure(time);
		timer.startTimeMeasure(time);
		jobDurationMap.computeIfPresent(jobStatus, (key, val) -> val + elapsedTime);
		jobStatus = newStatus;
	}
}
