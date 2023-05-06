package com.greencloud.commons.domain.job;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.immutables.value.Value;
import org.immutables.value.internal.$processor$.meta.$CriteriaMirrors;

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
public interface PowerJob extends Serializable {

	/**
	 * @return unique job identifier
	 */
	String getJobId();

	/**
	 * @return unique job instance identifier
	 */
	@$CriteriaMirrors.CriteriaId
	@Value.Default
	default String getJobInstanceId() {
		return UUID.randomUUID().toString();
	}

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
