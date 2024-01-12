package org.greencloud.domain;

import java.time.Instant;
import java.util.List;

import org.greencloud.enums.CompressionMethodEnum;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = ImmutableCompressedDataSent.class)
@JsonDeserialize(as = ImmutableCompressedDataSent.class)
@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CompressedDataSent {

	/**
	 * @return time when the data was sent
	 */
	Instant getDataSentTime();

	/**
	 * @return input data that was sent
	 */
	byte[] getInputData();

	/**
	 * @return input data original length
	 */
	Long getInputDataLength();

	/**
	 * @return method used to compress data
	 */
	CompressionMethodEnum getCompressionMethod();

	/**
	 * @return duration of data compression (in ms.)
	 */
	Long getCompressionDuration();

	/**
	 * @return optional additional parameters relevant to data compression/decompression
	 */
	@Nullable
	List<Object> getAdditionalParams();

}
