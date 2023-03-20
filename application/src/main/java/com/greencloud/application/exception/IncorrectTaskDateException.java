package com.greencloud.application.exception;

import static com.greencloud.application.exception.domain.ExceptionMessages.INCORRECT_DATE_FORMAT;

public class IncorrectTaskDateException extends RuntimeException {

	public IncorrectTaskDateException() {
		super(INCORRECT_DATE_FORMAT);
	}
}
