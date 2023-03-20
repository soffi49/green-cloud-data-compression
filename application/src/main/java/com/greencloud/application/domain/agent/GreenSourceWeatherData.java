package com.greencloud.application.domain.agent;

import org.immutables.value.Value.Immutable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.location.Location;

/**
 * Object storing the data passed by the Green Source in the weather query message
 */
@JsonSerialize(as = ImmutableGreenSourceWeatherData.class)
@JsonDeserialize(as = ImmutableGreenSourceWeatherData.class)
@Immutable
public interface GreenSourceWeatherData {

	/**
	 * @return location for which the weather is to be retrieved
	 */
	Location getLocation();

	double getPredictionError();
}
