package weather.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableForecast.class)
@JsonDeserialize(as = ImmutableForecast.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Immutable
public interface Forecast {

    /**
     * @return A number of timestamps returned in the API response of 5 day 3-hour forecast.
     */
    Integer getCnt();

    /**
     * @return List of {@link CurrentWeather} with 3-hour interval.
     */
    List<FutureWeather> getList();
}
