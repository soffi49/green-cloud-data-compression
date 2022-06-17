package weather.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableMain.class)
@JsonDeserialize(as = ImmutableMain.class)
@Immutable
public interface Main {

    Double getTemp();

    @JsonProperty("feels_like")
    Double getFeelsLike();

    @JsonProperty("temp_min")
    Double getMinimumTemperature();

    @JsonProperty("temp_max")
    Double getMaximumTemperature();

    Double getPressure();

    Double getHumidity();

    @JsonProperty("sea_level")
    Double getSeaLeve();

    @JsonProperty("grnd_level")
    Double getGroundLever();
}
