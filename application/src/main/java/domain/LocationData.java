package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableLocationData.class)
@JsonDeserialize(as = ImmutableLocationData.class)
@Value.Immutable
public interface LocationData {
    double getLatitude();
    double getLongitude();
}
