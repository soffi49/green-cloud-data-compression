package com.gui.message;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gui.message.domain.AdaptationAction;
import com.gui.message.domain.Message;

@Value.Immutable
@JsonSerialize(as = ImmutableUpdateAdaptationActionMessage.class)
@JsonDeserialize(as = ImmutableUpdateAdaptationActionMessage.class)
public interface UpdateAdaptationActionMessage extends Message {

	AdaptationAction getData();

	default String getType() {
		return "UPDATE_ADAPTATION_ACTION";
	}
}
