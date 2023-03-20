package com.greencloud.application.agents.server.constants;

import java.time.temporal.ValueRange;

/**
 * Class stores all predefined constants for Server Agent
 *
 * <p> MAX_AVAILABLE_POWER_DIFFERENCE       - range describing the negligible difference in available power of Green Sources </p>
 * <p> SERVER_ENVIRONMENT_SENSOR_TIMEOUT    - times between consecutive environments checks </p>
 * <p> SERVER_CHECK_POWER_SHORTAGE_JOBS     - times between consecutive checks of jobs affected by source power shortage </p>
 * <p> MAX_MESSAGE_NUMBER					- maximal number of messages that can be read at once by the Server Agent </p>
 * <p> TRANSFER_EXPIRATION_TIME			    - maximal time in milliseconds that Server will wait for transfer confirmation </p>
 */
public class ServerAgentConstants {

	public static final ValueRange MAX_AVAILABLE_POWER_DIFFERENCE = ValueRange.of(-10, 10);
	public static final long SERVER_ENVIRONMENT_SENSOR_TIMEOUT = 100;
	public static final long SERVER_CHECK_POWER_SHORTAGE_JOBS = 2000L;
	public static final int MAX_MESSAGE_NUMBER = 10;
	public static final long TRANSFER_EXPIRATION_TIME = 3000L;
}

