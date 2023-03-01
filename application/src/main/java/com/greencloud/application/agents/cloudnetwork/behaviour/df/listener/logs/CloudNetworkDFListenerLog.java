package com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.logs;

/**
 * Class contains all constants used in logging information in Cloud Network DF listener behaviours
 */
public class CloudNetworkDFListenerLog {

	// LISTEN FOR NETWORK CHANGE LOG MESSAGES
	public static final String FOUND_NEW_SERVERS_LOG = "Found {} new servers in the network!";
	public static final String FOUND_REMOVED_SERVERS_LOG = "Found {} removed servers in the network!";

	// DISABLE SERVER LOG MESSAGES
	public static final String DISABLING_SERVER_IN_CNA_LOG = "CNA is disabling Server {}.";
	public static final String SERVER_FOR_DISABLING_NOT_FOUND_LOG = "CNA didn't find the Server {} for disabling.";

}
