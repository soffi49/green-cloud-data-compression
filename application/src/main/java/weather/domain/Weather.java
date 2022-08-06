package weather.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableWeather.class)
@JsonDeserialize(as = ImmutableWeather.class)
@JsonInclude(Include.NON_NULL)
@Immutable
public interface Weather {

	Long getId();

	String getMain();

	String getDescription();

	String getIcon();
}
