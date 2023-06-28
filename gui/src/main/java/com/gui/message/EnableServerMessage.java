package com.gui.message;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = ImmutableEnableServerMessage.class)
@JsonDeserialize(as = ImmutableEnableServerMessage.class)
@Value.Immutable
public interface EnableServerMessage {

	String getServer();

	String getCna();

	double getCapacity();

	default String getType() {
		return "ENABLE_SERVER";
	}
}
