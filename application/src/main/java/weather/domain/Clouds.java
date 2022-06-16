package weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableClouds.class)
@JsonDeserialize(as = ImmutableClouds.class)
@Immutable
public interface Clouds {

    Double getAll();
}
