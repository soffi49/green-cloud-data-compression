package com.gui.message;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gui.message.domain.Message;

@Value.Immutable
@JsonSerialize(as = ImmutableUpdateJobExecutionProportionMessage.class)
@JsonDeserialize(as = ImmutableUpdateJobExecutionProportionMessage.class)
public interface UpdateJobExecutionProportionMessage extends Message {

	Double getData();

	String getAgentName();

	default String getType() {
		return "UPDATE_JOB_EXECUTION_PROPORTION";
	}
}
