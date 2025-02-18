package org.greencloud.managingsystem.agent.behaviour.executor.logs;

/**
 * Class contains all constants dedicated to log information in executor behaviours used by Managing Agent
 */
public final class ManagingExecutorLog {

	// INITIATE ADAPTATION ACTION LOG MESSAGES
	public static final String COMPLETED_ACTION_LOG = "Adaptation action id {} on agent {} at {} was executed successfully during {}!";
	public static final String COMPLETED_SYSTEM_ACTION_LOG = "Adaptation system action id {} at {} was executed successfully during {}!";
	public static final String ACTION_FAILED_LOG = "Adaptation action id {} on agent {} execution failed!";
	public static final String ACTION_SYSTEM_FAILED_LOG = "Adaptation system action id {} execution failed!";

	// VERIFY ADAPTATION ACTION RESULT LOG MESSAGES
	public static final String VERIFY_ACTION_START_LOG = "Verifying {} executed on {} at {}.";
	public static final String VERIFY_ACTION_END_LOG = "Action {} verified with following goal changes: {}";
}
