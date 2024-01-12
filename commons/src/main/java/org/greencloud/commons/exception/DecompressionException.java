package org.greencloud.commons.exception;

import static org.greencloud.commons.exception.domain.ExceptionMessages.DECOMPRESSION_FAILED;

/**
 * Exception thrown when date could not be decompressed
 */
public class DecompressionException extends RuntimeException {

	public DecompressionException() {
		super(DECOMPRESSION_FAILED);
	}
}
