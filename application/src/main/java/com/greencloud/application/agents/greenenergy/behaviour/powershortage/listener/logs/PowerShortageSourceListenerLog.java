package com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.logs;

/**
 * Class contains all constants used in logging information in listener behaviours for power shortage in green source
 */
public class PowerShortageSourceListenerLog {

	// POWER SHORTAGE IN SERVER RECEIVED START INFORMATION LOG MESSAGES
	public static final String SERVER_POWER_SHORTAGE_START_LOG =
			"Received information about job {} power shortage in server. Updating green source state";
	public static final String SERVER_POWER_SHORTAGE_START_NOT_FOUND_LOG =
			"Job {} to divide due to power shortage was not found";

	// POWER SHORTAGE IN SERVER RECEIVED FINISH INFORMATION LOG MESSAGES
	public static final String SERVER_POWER_SHORTAGE_FINISH_CHANGE_LOG =
			"Power shortage in server finished. Changing the status of the server job {}";
	public static final String SERVER_POWER_SHORTAGE_FINISH_NOT_FOUND_LOG =
			"Job {} to supply with green power was not found";

	//POWER SHORTAGE IN SERVER TRANSFER FAILURE LOG MESSAGES
	public static final String SERVER_POWER_SHORTAGE_FAILURE_PUT_ON_HOLD_LOG =
			"Received information about job {} transfer failure. Putting job on hold";
	public static final String SERVER_POWER_SHORTAGE_FAILURE_NOT_FOUND_LOG =
			"Job {} to put on hold was not found";

	// RE-SUPPLYING JOB WITH GREEN ENERGY LOG MESSAGES
	public static final String SERVER_JOB_RE_SUPPLY_REQUEST_LOG =
			"Verifying if job {} can be supplied with green energy";
	public static final String SERVER_JOB_RE_SUPPLY_REQUEST_NOT_FOUND_LOG =
			"Job {} is no longer existing in given green energy source";
	public static final String WEATHER_UNAVAILABLE_RE_SUPPLY_JOB_LOG =
			"The data for the job is not available. Job {} cannot be supplied with green energy";
	public static final String RE_SUPPLY_FAILURE_NO_POWER_JOB_LOG =
			"There is not enough available power (needed {}, have {}). Job {} cannot be supplied with green energy";
	public static final String RE_SUPPLY_FAILURE_JOB_NOT_FOUND_LOG =
			"Job {} cannot be supplied with green energy - job not found";
	public static final String RE_SUPPLY_JOB_WITH_GREEN_ENERGY_LOG =
			"Job {} is being supplied again using the green energy";

}
