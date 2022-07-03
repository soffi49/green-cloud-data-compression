package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.OffsetDateTime;
import java.util.List;

@JsonSerialize(as = ImmutableJobTransfer.class)
@JsonDeserialize(as = ImmutableJobTransfer.class)
@Value.Immutable
public interface JobTransfer {

    List<PowerJob> getJobList();

    OffsetDateTime getStartTime();
}
