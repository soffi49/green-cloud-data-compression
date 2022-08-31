package domain.powershortage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

import java.time.OffsetDateTime;

import domain.job.JobInstanceIdentifier;

/**
 * Object stores the data necessary to perform job transfer
 */
@JsonSerialize(as = ImmutablePowerShortageJob.class)
@JsonDeserialize(as = ImmutablePowerShortageJob.class)
@Value.Immutable
public interface PowerShortageJob {

	/**
	 * @return unique job identifier
	 */
	JobInstanceIdentifier getJobInstanceId();

	/**
	 * @return time when transfer will happen
	 */
	OffsetDateTime getPowerShortageStart();
}
