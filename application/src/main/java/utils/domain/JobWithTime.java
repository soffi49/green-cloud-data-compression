package utils.domain;

import java.time.Instant;

import domain.job.Job;

/**
 * Class which maps the job and its time instance and is used in the power calculation algorithm
 */
public class JobWithTime {

	public final Job job;
	public final Instant time;
	public final TimeType timeType;

	public JobWithTime(Job job, Instant time, TimeType timeType) {
		this.job = job;
		this.time = time;
		this.timeType = timeType;
	}

	public enum TimeType {
		START_TIME, END_TIME
	}
}
