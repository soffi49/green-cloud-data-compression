package com.database.knowledge.domain.agent.client;

import org.immutables.value.Value;

import com.database.knowledge.domain.agent.MonitoringData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Interface storing information about what percentage of the job was executed for the client
 */
@JsonDeserialize(as = ImmutableClientJobExecutionData.class)
@JsonSerialize(as = ImmutableClientJobExecutionData.class)
@Value.Immutable
public interface ClientJobExecutionData extends MonitoringData {

	/**
	 * @return percentage of what part of the job was executed
	 * (i.e. out of the total time of the job execution, for how long was the job really executed)
	 */
	double getJobExecutionPercentage();
}
