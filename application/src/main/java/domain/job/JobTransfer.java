package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.OffsetDateTime;

/**
 * Object stores the data necessary to perform job transfer
 */
@JsonSerialize(as = ImmutableJobTransfer.class)
@JsonDeserialize(as = ImmutableJobTransfer.class)
@Value.Immutable
public interface JobTransfer {

    /**
     * @return unique job identifier
     */
    String getJobId();

    /**
     * @return time when transfer will happen
     */
    OffsetDateTime getTransferTime();
}