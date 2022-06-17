package weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableWeather.class)
@JsonDeserialize(as = ImmutableWeather.class)
@Immutable
public interface Weather {

    Long getId();

    String getMain();

    String getDescription();

    String getIcon();
}
