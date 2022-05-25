package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import domain.location.Location;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableServerRequestData.class)
@JsonDeserialize(as = ImmutableServerRequestData.class)
@Value.Immutable
public interface ServerRequestData {

    String getJobId();

    Location getLocation();
}
