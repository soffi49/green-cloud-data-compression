package domain.location;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonDeserialize(as = ImmutableLocation.class)
@JsonSerialize(as = ImmutableLocation.class)
@Value.Immutable
public interface Location {

    double getLatitude();

    double getLongitude();
}
