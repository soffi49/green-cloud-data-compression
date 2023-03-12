package com.gui.message;

import java.util.Map;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.job.enums.JobClientStatusEnum;

@JsonSerialize(as = ImmutableSetClientJobDurationMapMessage.class)
@JsonDeserialize(as = ImmutableSetClientJobDurationMapMessage.class)
@Value.Immutable
public interface SetClientJobDurationMapMessage {

	Map<JobClientStatusEnum, Long> getData();

	String getAgentName();

	default String getType() {
		return "SET_CLIENT_JOB_DURATION_MAP";
	}
}
