package utils.domain;

import java.time.OffsetDateTime;

import domain.job.Job;

/**
 * Class which maps the job and its time instance and is used in the power calculation algorithm
 */
public class JobWithTime {

	public final Job job;
	public final OffsetDateTime time;
	public final TimeType timeType;

	public JobWithTime(Job job, OffsetDateTime time, TimeType timeType) {
		this.job = job;
		this.time = time;
		this.timeType = timeType;
	}

	public enum TimeType {
		START_TIME, END_TIME
	}
}
