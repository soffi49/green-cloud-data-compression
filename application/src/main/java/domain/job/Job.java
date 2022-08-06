package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.OffsetDateTime;

import org.immutables.value.Value.Immutable;

/**
 * Object storing the data describing the client's job
 */
@JsonSerialize(as = ImmutableJob.class)
@JsonDeserialize(as = ImmutableJob.class)
@Immutable
public interface Job {

	/**
	 * @return unique job identifier
	 */
	String getJobId();

	/**
	 * @return unique client identifier (client global name)
	 */
	String getClientIdentifier();

	/**
	 * @return time when the job execution should start
	 */
	OffsetDateTime getStartTime();

	/**
	 * @return time when the job execution should finish
	 */
	OffsetDateTime getEndTime();

	/**
	 * @return power necessary to execute the job
	 */
	int getPower();
}
