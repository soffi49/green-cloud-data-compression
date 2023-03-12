package com.greencloud.application.domain.job;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.ImmutableConfig;

/**
 * Object stores the data necessary to perform job transfer
 */
@JsonSerialize(as = ImmutableJobPowerShortageTransfer.class)
@JsonDeserialize(as = ImmutableJobPowerShortageTransfer.class)
@Value.Immutable
@ImmutableConfig
public interface JobPowerShortageTransfer {

	/**
	 * @return unique job identifier
	 */
	JobInstanceIdentifier getJobInstanceId();

	/**
	 * @return time when transfer will happen
	 */
	Instant getPowerShortageStart();
}
