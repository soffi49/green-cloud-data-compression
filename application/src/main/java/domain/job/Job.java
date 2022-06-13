package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableJob.class)
@JsonDeserialize(as = ImmutableJob.class)
@Immutable
public interface Job {

    String getJobId();

    String getClientIdentifier();

    OffsetDateTime getStartTime();

    OffsetDateTime getEndTime();

    int getPower();
}
