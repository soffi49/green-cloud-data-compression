package com.greencloud.application.agents.client.constants;

import java.time.temporal.ValueRange;

/**
 * Class storing Client Agent constants:
 * <p> SCHEDULER_AGENT	- data store key under which the Scheduler Agent found in the DF will be stored </p>
 * <p> MAX_TIME_DIFFERENCE	- maximum job delay time </p>
 */
public class ClientAgentConstants {
	public static final String SCHEDULER_AGENT = "SCHEDULER_AGENT";
	public static final ValueRange MAX_TIME_DIFFERENCE = ValueRange.of(-200, 200);
}
