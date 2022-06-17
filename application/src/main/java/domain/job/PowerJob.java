package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.OffsetDateTime;

/**
 * Object storing the data describing power request send to the green source:
 * <p>
 * jobId - unique identifier of the given job
 * <p>
 * startTime - time when the source should start the power delivery
 * <p>
 * endTime - time when the source should finish power delivery
 * <p>
 * power - power requested by the server
 */
@JsonSerialize(as = ImmutablePowerJob.class)
@JsonDeserialize(as = ImmutablePowerJob.class)
@Value.Immutable
public interface PowerJob {

    String getJobId();

    OffsetDateTime getStartTime();

    OffsetDateTime getEndTime();

    int getPower();
}
