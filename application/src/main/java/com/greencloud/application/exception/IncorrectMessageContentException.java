package com.greencloud.application.exception;

import static com.greencloud.application.exception.domain.ExceptionMessages.INCORRECT_MESSAGE_FORMAT;

public class IncorrectMessageContentException extends RuntimeException {

	public IncorrectMessageContentException() {
		super(INCORRECT_MESSAGE_FORMAT);
	}

}
