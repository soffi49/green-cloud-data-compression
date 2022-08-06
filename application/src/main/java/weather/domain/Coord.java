package weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableCoord.class)
@JsonDeserialize(as = ImmutableCoord.class)
@Immutable
public interface Coord {

	Double getLon();

	Double getLat();
}
