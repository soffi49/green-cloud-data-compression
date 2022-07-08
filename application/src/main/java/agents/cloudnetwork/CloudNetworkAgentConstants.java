package agents.cloudnetwork;

import java.time.temporal.ValueRange;

/**
 * Class storing Cloud Network Agent constants:
 * <p>
 * MAX_POWER_DIFFERENCE - value range describing the maximum difference in free power of the Server Agents that can be
 * neglected in Server selection
 * <p>
 * SERVER_AGENTS - data store key under which the Server Agents found in the DF will be stored
 * <p>
 * MESSAGE_DELAY_TIMEOUT - acceptable timeout of the job starting delay
 * <p>
 * MAX_ERROR_IN_JOB_START - time error added to the time after which job execution should start
 */
public class CloudNetworkAgentConstants {

    public static final ValueRange MAX_POWER_DIFFERENCE = ValueRange.of(-10, 10);
    public static final String SERVER_AGENTS = "SERVER_AGENTS_LIST";
    public static final Long MAX_ERROR_IN_JOB_START = 1000L;
    public static final Long RETRY_PAUSE_MILLISECONDS = 500L;
    public static final Integer RETRY_LIMIT = 3;
}
