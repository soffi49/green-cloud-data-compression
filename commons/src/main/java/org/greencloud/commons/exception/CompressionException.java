package org.greencloud.commons.exception;

import static org.greencloud.commons.exception.domain.ExceptionMessages.COMPRESSION_FAILED;

/**
 * Exception thrown when date could not be compressed
 */
public class CompressionException extends RuntimeException {

	public CompressionException() {
		super(COMPRESSION_FAILED);
	}
}
