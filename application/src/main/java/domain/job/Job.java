package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.OffsetDateTime;
import org.immutables.value.Value.Immutable;

/**
 * Object storing the data describing client's job:
 * <p>
 * jobId - unique identifier of the given job
 * <p>
 * clientIdentifier - unique client identifier (client global name)
 * <p>
 * startTime - time when the job execution should start
 * <p>
 * endTime - time when the job execution should finish
 * <p>
 * power - power necessary to execute the given job
 */
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
