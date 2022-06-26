package weather.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.jetbrains.annotations.Nullable;

@JsonSerialize(as = ImmutableWind.class)
@JsonDeserialize(as = ImmutableWind.class)
@JsonInclude(Include.NON_NULL)
@Immutable
public interface Wind {

    Double getSpeed();

    Double getDeg();

    @Nullable
    Double getGust();
}
