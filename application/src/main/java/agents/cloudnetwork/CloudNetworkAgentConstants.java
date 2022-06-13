package agents.cloudnetwork;

import java.time.temporal.ValueRange;

/**
 * Class storing Cloud Network Agent constants:
 * <p>
 * <p>
 * MAX_POWER_DIFFERENCE - value range describing the maximum difference in free power of the Server Agents that can be
 * neglected in Server selection
 * <p>
 * SERVER_AGENTS - data store key under which the Server Agents found in the DF will be stored
 */
public class CloudNetworkAgentConstants {

    public static final ValueRange MAX_POWER_DIFFERENCE = ValueRange.of(-10, 10);
    public static final String SERVER_AGENTS = "SERVER_AGENTS_LIST";
}
