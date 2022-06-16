package weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableWind.class)
@JsonDeserialize(as = ImmutableWind.class)
@Immutable
public interface Wind {

    Double getSpeed();

    Double getDeg();

    Double getGust();
}
