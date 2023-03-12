package com.greencloud.application.domain.job;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.ImmutableConfig;

/**
 * Object stores the information about the time of job status update
 */
@JsonSerialize(as = ImmutableJobStatusUpdate.class)
@JsonDeserialize(as = ImmutableJobStatusUpdate.class)
@Value.Immutable
@ImmutableConfig
public interface JobStatusUpdate {

	/**
	 * @return job of interest
	 */
	JobInstanceIdentifier getJobInstance();

	/**
	 * @return time of status change
	 */
	Instant getChangeTime();
}
