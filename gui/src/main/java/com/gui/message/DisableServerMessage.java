package com.gui.message;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = ImmutableDisableServerMessage.class)
@JsonDeserialize(as = ImmutableDisableServerMessage.class)
@Value.Immutable
public interface DisableServerMessage {

	String getServer();

	String getCna();

	double getCapacity();

	default String getType() {
		return "DISABLE_SERVER";
	}
}
