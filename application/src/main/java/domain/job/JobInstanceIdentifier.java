package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.OffsetDateTime;

/**
 * Object stores the data which allow to identify two instances of the job with the same jobId
 */
@JsonSerialize(as = ImmutableJobInstanceIdentifier.class)
@JsonDeserialize(as = ImmutableJobInstanceIdentifier.class)
@Value.Immutable
public interface JobInstanceIdentifier {

    /**
     * @return unique job id
     */
    String getJobId();

    /**
     * @return job start time
     */
    OffsetDateTime getStartTime();
}
