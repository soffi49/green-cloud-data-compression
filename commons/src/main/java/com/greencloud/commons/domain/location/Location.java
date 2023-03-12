package com.greencloud.commons.domain.location;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.ImmutableConfig;

/**
 * Object storing the location details
 */
@JsonSerialize(as = ImmutableLocation.class)
@JsonDeserialize(as = ImmutableLocation.class)
@Value.Immutable
@ImmutableConfig
public interface Location {

	/**
	 * @return location latitude
	 */
	double getLatitude();

	/**
	 * @return location longitude
	 */
	double getLongitude();
}
