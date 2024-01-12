package org.greencloud.commons.mapper;

import java.util.HashMap;
import java.util.Map;

import org.greencloud.commons.domain.resources.ImmutableResource;
import org.greencloud.commons.domain.resources.Resource;
import org.greencloud.commons.domain.resources.ResourceCharacteristic;

/**
 * Class containing common methods to map resources
 */
public class ResourceMapper {

	/**
	 * @param resource          current resource
	 * @param newCharacteristic characteristic to be added
	 * @param key               characteristic identifier
	 * @return updated Resource
	 */
	public static Resource mapToResourceWithNewCharacteristic(final Resource resource,
			final ResourceCharacteristic newCharacteristic, final String key) {
		final Map<String, ResourceCharacteristic> characteristics = new HashMap<>(resource.getCharacteristics());
		characteristics.put(key, newCharacteristic);
		return ImmutableResource.copyOf(resource).withCharacteristics(characteristics);
	}
}
