package org.greencloud.commons.domain.job.basic;

import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.DATA;
import static org.greencloud.commons.constants.resource.ResourceCharacteristicConstants.INPUT;
import static org.greencloud.commons.mapper.ResourceMapper.mapToResourceWithNewCharacteristic;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.greencloud.commons.domain.ImmutableConfig;
import org.greencloud.commons.domain.resources.ImmutableResourceCharacteristic;
import org.greencloud.commons.domain.resources.Resource;
import org.greencloud.commons.domain.resources.ResourceCharacteristic;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Object storing the data describing the client's job
 */
@JsonSerialize(as = ImmutableClientJob.class)
@JsonDeserialize(as = ImmutableClientJob.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Value.Immutable
@ImmutableConfig
public interface ClientJob extends PowerJob {

	/**
	 * @return unique client identifier (client global name)
	 */
	String getClientIdentifier();

	/**
	 * @return unique client identifier (client global name)
	 */
	String getClientAddress();

	/**
	 * @return optional server selection preference specified in Expression Language
	 */
	@Nullable
	String getSelectionPreference();

	/**
	 * Method returns copy of current job updated with input data.
	 *
	 * @param inputData input data
	 * @return new job
	 */
	default ClientJob addInputDataToJobResources(final byte[] inputData) {
		final Resource inputResource = getRequiredResources().get(INPUT);
		final ResourceCharacteristic dataCharacteristic = ImmutableResourceCharacteristic.builder()
				.value(inputData)
				.isRequired(false)
				.build();
		final Resource updatedResource = mapToResourceWithNewCharacteristic(inputResource, dataCharacteristic, DATA);
		final Map<String, Resource> newResources = new HashMap<>(getRequiredResources());
		newResources.replace(INPUT, updatedResource);

		return ImmutableClientJob.copyOf(this).withRequiredResources(newResources);
	}

}
