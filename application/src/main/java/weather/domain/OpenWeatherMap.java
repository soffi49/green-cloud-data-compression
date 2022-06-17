package weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableOpenWeatherMap.class)
@JsonDeserialize(as = ImmutableOpenWeatherMap.class)
@Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
public interface OpenWeatherMap {

    Coord getCoord();

    List<Weather> getWeather();

    Main getMain();

    Wind getWind();

    Clouds getClouds();

    Double getVisibility();

    Sys getSys();

    Long getTimezone();
}
