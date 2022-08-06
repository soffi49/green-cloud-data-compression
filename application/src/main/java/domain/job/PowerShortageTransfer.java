package domain.job;

import java.time.OffsetDateTime;
import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Object stores the data necessary to perform the transfer in case of the power shortage
 */
@JsonSerialize(as = ImmutablePowerShortageTransfer.class)
@JsonDeserialize(as = ImmutablePowerShortageTransfer.class)
@Value.Immutable
public interface PowerShortageTransfer {

	//TODO make it smaller!!!!

	/**
	 * @return jobs that need to be transferred
	 */
	List<PowerJob> getJobList();

	/**
	 * @return time when the transfer should happen
	 */
	OffsetDateTime getStartTime();
}
