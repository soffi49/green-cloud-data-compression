package com.greencloud.application.exception;

import static com.greencloud.application.exception.domain.ExceptionMessages.JOB_NOT_FOUND_ERROR;

public class JobNotFoundException extends RuntimeException {

	public JobNotFoundException() {
		super(JOB_NOT_FOUND_ERROR);
	}
}
