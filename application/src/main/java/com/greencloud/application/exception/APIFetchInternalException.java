package com.greencloud.application.exception;

import static com.greencloud.application.exception.domain.ExceptionMessages.WEATHER_API_INTERNAL_ERROR;

public class APIFetchInternalException extends RuntimeException {

	public APIFetchInternalException() {
		super(WEATHER_API_INTERNAL_ERROR);
	}
}
