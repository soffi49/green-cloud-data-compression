package com.greencloud.commons.domain.job;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.ImmutableConfig;

/**
 * Object storing the data describing job that is to be executed in Cloud
 */
@JsonSerialize(as = ImmutablePowerJob.class)
@JsonDeserialize(as = ImmutablePowerJob.class)
@Value.Immutable
@ImmutableConfig
public interface PowerJob {

	/**
	 * @return unique job identifier
	 */
	String getJobId();

	/**
	 * @return time when the power delivery should start
	 */
	Instant getStartTime();

	/**
	 * @return time when the power delivery should finish
	 */
	Instant getEndTime();

	/**
	 * @return time before which job has to end
	 */
	Instant getDeadline();

	/**
	 * @return power that is to be delivered
	 */
	int getPower();
}
