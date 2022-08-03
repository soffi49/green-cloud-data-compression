package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Object stores the data necessary to perform the transfer in case of the power shortage
 */
@JsonSerialize(as = ImmutablePowerShortageTransfer.class)
@JsonDeserialize(as = ImmutablePowerShortageTransfer.class)
@Value.Immutable
public interface PowerShortageTransfer {

	/**
	 * @return jobs that need to be transferred
	 */
	List<PowerJob> getJobList();

	/**
	 * @return time when the transfer should happen
	 */
	OffsetDateTime getStartTime();
}
