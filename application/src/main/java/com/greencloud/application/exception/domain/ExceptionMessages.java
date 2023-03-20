package com.greencloud.application.exception.domain;

/**
 * Class stores all constants displayed as exception messages
 */
public class ExceptionMessages {

	public static final String INCORRECT_MESSAGE_FORMAT = "The provided message content has incorrect format";
	public static final String INCORRECT_AGENT_TYPE = "The provided agent type %s is incorrect";
	public static final String INCORRECT_DATE_FORMAT = "The provided execution date has incorrect format";
	public static final String WEATHER_API_INTERNAL_ERROR = "The API retrieved null instead of the weather data";
	public static final String JOB_NOT_FOUND_ERROR = "Job does not exists in given agent";
}
