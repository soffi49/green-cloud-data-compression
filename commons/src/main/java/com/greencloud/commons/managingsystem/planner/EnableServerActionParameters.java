package com.greencloud.commons.managingsystem.planner;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableEnableServerActionParameters.class)
@JsonSerialize(as = ImmutableEnableServerActionParameters.class)
public interface EnableServerActionParameters extends AdaptationActionParameters {

	@Override
	default boolean dependsOnOtherAgents() {
		return true;
	}
}
