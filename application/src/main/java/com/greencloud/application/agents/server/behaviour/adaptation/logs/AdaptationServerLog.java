package com.greencloud.application.agents.server.behaviour.adaptation.logs;

/**
 * Class contains all constants used in logging information in adaptation behaviours of the given Server agent
 */
public class AdaptationServerLog {

	// INITIATE SERVER DISABLING LOG MESSAGES
	public static final String DISABLING_FAILED_LOG =
			"Disabling server failed - Server {} does not exists in a given Cloud Network.";
	public static final String DISABLING_SUCCEEDED_LOG = "Server was successfully disabled in Cloud Network {}.";
	public static final String DISABLING_LEFT_JOBS_LOG =
			"Server will finish executing {} planned jobs before being fully disabled.";

	// COMPLETE SERVER DISABLING LOG MESSAGES
	public static final String DISABLING_COMPLETED_LOG = "Server completed all planned jobs and is fully disabled.";
}
