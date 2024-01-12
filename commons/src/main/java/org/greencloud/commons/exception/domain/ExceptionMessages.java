package org.greencloud.commons.exception.domain;

/**
 * Class stores all constants displayed as exception messages
 */
public class ExceptionMessages {
	public static final String INCORRECT_AGENT_TYPE = "The provided agent type %s is incorrect";
	public static final String INCORRECT_DATE_FORMAT = "The provided execution date has incorrect format";
	public static final String COMPRESSION_FAILED = "The data compression has failed.";
	public static final String DECOMPRESSION_FAILED = "The data decompression has failed.";
	public static final String WEATHER_API_INTERNAL_ERROR = "The API retrieved null instead of the weather data";
}
