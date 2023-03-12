package com.greencloud.commons.domain.job;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.ImmutableConfig;

/**
 * Object storing the data describing the client's job
 */
@JsonSerialize(as = ImmutableClientJob.class)
@JsonDeserialize(as = ImmutableClientJob.class)
@Value.Immutable
@ImmutableConfig
public interface ClientJob extends PowerJob {

	/**
	 * @return unique client identifier (client global name)
	 */
	String getClientIdentifier();

}
