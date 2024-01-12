package com.database.knowledge.domain.agent.server;

import org.greencloud.enums.CompressionMethodEnum;
import org.immutables.value.Value;

import com.database.knowledge.domain.agent.MonitoringData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Monitoring data storing information about data transmission
 */
@Value.Immutable
@JsonSerialize(as = ImmutableServerTransmissionData.class)
@JsonDeserialize(as = ImmutableServerTransmissionData.class)
public interface ServerTransmissionData extends MonitoringData {

	Long getMessageRetrievalDuration();
	Long getCompressionTime();
	Long getDecompressionTime();
	CompressionMethodEnum getCompressionMethod();
	Double getBytesSentToBytesReceived();
	Long getEstimatedTransferCost();
	Long getTransferredSize();
	Double getCompressionRatio();
}
