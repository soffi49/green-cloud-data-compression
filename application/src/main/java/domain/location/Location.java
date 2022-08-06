package domain.location;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

/**
 * Object storing the location details
 */
@JsonDeserialize(as = ImmutableLocation.class)
@JsonSerialize(as = ImmutableLocation.class)
@Value.Immutable
public interface Location {

	/**
	 * @return location latitude
	 */
	double getLatitude();

	/**
	 * @return location longitude
	 */
	double getLongitude();
}
