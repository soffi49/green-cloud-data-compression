package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableCheckedPowerJob.class)
@JsonDeserialize(as = ImmutableCheckedPowerJob.class)
@Immutable
public interface CheckedPowerJob {

	public PowerJob getPowerJob();

	public Boolean informCNAStart();

	public Boolean informCNAFinish();
}
