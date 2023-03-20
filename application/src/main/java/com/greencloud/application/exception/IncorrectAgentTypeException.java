package com.greencloud.application.exception;

import static com.greencloud.application.exception.domain.ExceptionMessages.INCORRECT_AGENT_TYPE;
import static java.lang.String.format;

public class IncorrectAgentTypeException extends RuntimeException {

	public IncorrectAgentTypeException(final String agentType) {
		super(format(INCORRECT_AGENT_TYPE, agentType));
	}
}
