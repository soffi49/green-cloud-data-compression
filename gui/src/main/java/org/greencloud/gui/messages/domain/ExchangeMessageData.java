package org.greencloud.gui.messages.domain;

import org.greencloud.enums.CompressionMethodEnum;
import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableExchangeMessageData.class)
@JsonDeserialize(as = ImmutableExchangeMessageData.class)
public interface ExchangeMessageData {

	Long getMessageRetrievalDuration();

	Long getCompressionTime();

	Long getDecompressionTime();

	CompressionMethodEnum getCompressionMethod();

	Double getBytesSentToBytesReceived();

	Long getEstimatedTransferCost();
	Long getTransferredSize();
	Double getCompressionRatio();
}
