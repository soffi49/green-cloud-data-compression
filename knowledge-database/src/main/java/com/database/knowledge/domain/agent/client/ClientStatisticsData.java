package com.database.knowledge.domain.agent.client;

import org.immutables.value.Value;

import com.database.knowledge.domain.agent.MonitoringData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Interface storing statistics used in scenario analysis sent by the Client Agent
 */
@JsonDeserialize(as = ImmutableClientStatisticsData.class)
@JsonSerialize(as = ImmutableClientStatisticsData.class)
@Value.Immutable
public interface ClientStatisticsData extends MonitoringData {

	/**
	 * @return time in ms that it took for a message to be sent from the server to the Client
	 */
	long getMessageRetrievalTime();
}
