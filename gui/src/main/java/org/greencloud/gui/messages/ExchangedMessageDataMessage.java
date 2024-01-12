package org.greencloud.gui.messages;

import org.greencloud.gui.messages.domain.ExchangeMessageData;
import org.greencloud.gui.messages.domain.Message;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = ImmutableExchangedMessageDataMessage.class)
@JsonDeserialize(as = ImmutableExchangedMessageDataMessage.class)
@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ExchangedMessageDataMessage extends Message {

	String getAgentName();

	ExchangeMessageData getData();

	default String getType() {
		return "ADD_EXCHANGED_MESSAGE_DATA";
	}
}
