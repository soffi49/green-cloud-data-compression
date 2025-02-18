package org.greencloud.gui.messages.domain;

import java.time.Instant;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableSplitJob.class)
@JsonDeserialize(as = ImmutableSplitJob.class)
public interface SplitJob {

	String getSplitJobId();

	double getPower();

	Instant getStart();

	Instant getEnd();
}
