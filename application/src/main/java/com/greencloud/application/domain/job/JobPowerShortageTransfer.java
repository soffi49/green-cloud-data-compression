package com.greencloud.application.domain.job;

import java.time.Instant;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

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
	 * @return identifier of previous job instance
	 */
	@Nullable
	String getOriginalJobInstanceId();

	/**
	 * @return identifier of the first instance of divided job
	 */
	@Nullable
	JobInstanceIdentifier getFirstJobInstanceId();

	/**
	 * @return identifier of the second instance of divided job
	 */
	JobInstanceIdentifier getSecondJobInstanceId();

	/**
	 * @return time when transfer will happen
	 */
	Instant getPowerShortageStart();
}
