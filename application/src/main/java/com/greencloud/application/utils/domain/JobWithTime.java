package com.greencloud.application.utils.domain;

import java.time.Instant;

import com.greencloud.commons.job.PowerJob;

/**
 * Class which maps the job and its time instance and is used in the power calculation algorithm
 */
public class JobWithTime<T extends PowerJob> {

	public final T job;
	public final Instant time;
	public final TimeType timeType;

	public JobWithTime(T job, Instant time, TimeType timeType) {
		this.job = job;
		this.time = time;
		this.timeType = timeType;
	}

	public enum TimeType {
		START_TIME, END_TIME
	}
}
