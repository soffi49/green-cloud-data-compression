package weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableSys.class)
@JsonDeserialize(as = ImmutableSys.class)
@Immutable
public interface Sys {

    String getCountry();

    String getSunrise();

    String getSunset();
}
