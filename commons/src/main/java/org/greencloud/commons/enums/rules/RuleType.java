package org.greencloud.commons.enums.rules;

/**
 * Enum with possible rule types
 */
public class RuleType {

	/**
	 * DEFAULT RULES
	 */
	public static final String BASIC_RULE = "BASIC_RULE";
	public static final String BASIC_CFP_RULE = "BASIC_CFP_RULE";
	public static final String BASIC_PROPOSAL_RULE = "BASIC_PROPOSAL_RULE";
	public static final String BASIC_LISTENER = "BASIC_LISTENER";
	public static final String BASIC_LISTENER_HANDLER = "BASIC_LISTENER_HANDLER";
	public static final String BASIC_COMPARATOR_RULE = "BASIC_COMPARATOR_RULE";

	/**
	 * INITIALIZATION RULES
	 */
	public static final String INITIALIZE_BEHAVIOURS_RULE = "INITIALIZE_BEHAVIOURS_RULE";

	/**
	 * ADAPTATION RULES
	 */
	public static final String ADAPTATION_REQUEST_RULE = "ADAPTATION_REQUEST_RULE";
	public static final String AGENT_CREATION_RULE = "AGENT_CREATION_RULE";
	public static final String AGENT_MODIFY_RULE_SET_RULE = "AGENT_MODIFY_RULE_SET_RULE";
	public static final String SERVER_MAINTENANCE_RULE = "SERVER_MAINTENANCE_RULE";
	public static final String REPORT_DATA_RULE = "REPORT_DATA_RULE";
	public static final String PROCESS_SERVER_ENABLING_RULE = "PROCESS_SERVER_ENABLING_RULE";
	public static final String PROCESS_SERVER_DISABLING_RULE = "PROCESS_SERVER_DISABLING_RULE";
	public static final String PROCESS_SERVER_CONNECTION_RULE = "PROCESS_SERVER_CONNECTION_RULE";
	public static final String PROCESS_SERVER_DEACTIVATION_RULE = "PROCESS_SERVER_DEACTIVATION_RULE";
	public static final String PROCESS_SERVER_DISCONNECTION_RULE = "PROCESS_SERVER_DISCONNECTION_RULE";
	public static final String REQUEST_RULE_SET_UPDATE_RULE = "REQUEST_RULE_SET_UPDATE_RULE";
	public static final String LISTEN_FOR_RULE_SET_UPDATE_RULE = "LISTEN_FOR_RULE_SET_UPDATE_RULE";
	public static final String LISTEN_FOR_RULE_SET_UPDATE_HANDLER_RULE = "LISTEN_FOR_RULE_SET_UPDATE_HANDLER_RULE";
	public static final String LISTEN_FOR_RULE_SET_REMOVAL_RULE = "LISTEN_FOR_RULE_SET_REMOVAL_RULE";
	public static final String LISTEN_FOR_RULE_SET_REMOVAL_HANDLER_RULE = "LISTEN_FOR_RULE_SET_REMOVAL_HANDLER_RULE";

	/**
	 * EVENT RULES
	 */
	public static final String SENSE_EVENTS_RULE = "SENSE_EVENTS_RULE";

	/**
	 * TRANSFER RULES
	 */
	public static final String LISTEN_FOR_JOB_TRANSFER_RULE = "LISTEN_FOR_JOB_TRANSFER_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLE_JOB_NOT_FOUND_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLE_JOB_NOT_FOUND_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_NO_SERVERS_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_NO_SERVERS_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_SEND_TRANSFER_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_SEND_TRANSFER_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_CONFIRMATION_RULE = "LISTEN_FOR_JOB_TRANSFER_CONFIRMATION_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_IN_RMA_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_IN_RMA_RULE";
	public static final String LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_IN_GREEN_SOURCE_RULE = "LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_IN_GREEN_SOURCE_RULE";
	public static final String PROCESS_TRANSFER_REQUEST_RULE = "PROCESS_TRANSFER_REQUEST_RULE";
	public static final String REFUSED_TRANSFER_JOB_RULE = "REFUSED_TRANSFER_JOB_RULE";
	public static final String REFUSED_TRANSFER_JOB_NOT_FOUND_RULE = "REFUSED_TRANSFER_JOB_NOT_FOUND_RULE";
	public static final String REFUSED_TRANSFER_JOB_ALREADY_FINISHED_RULE = "REFUSED_TRANSFER_JOB_ALREADY_FINISHED_RULE";
	public static final String REFUSED_TRANSFER_JOB_EXISTING_JOB_RULE = "REFUSED_TRANSFER_JOB_EXISTING_JOB_RULE";
	public static final String TRANSFER_JOB_RULE = "TRANSFER_JOB_RULE";
	public static final String TRANSFER_JOB_IN_GS_RULE = "TRANSFER_JOB_IN_GS_RULE";
	public static final String TRANSFER_JOB_FOR_GS_IN_RMA_RULE = "TRANSFER_JOB_FOR_GS_IN_RMA_RULE";
	public static final String PROCESS_TRANSFER_UPDATE_CONFIRMATION_RULE = "PROCESS_TRANSFER_UPDATE_CONFIRMATION_RULE";
	public static final String PROCESS_TRANSFER_UPDATE_FAILURE_RULE = "PROCESS_TRANSFER_UPDATE_FAILURE_RULE";

	/**
	 * POWER SHORTAGE RULES
	 */
	public static final String POWER_SHORTAGE_ERROR_RULE = "POWER_SHORTAGE_ERROR_RULE";
	public static final String POWER_SHORTAGE_ERROR_FINISH_RULE = "POWER_SHORTAGE_ERROR_FINISH_RULE";
	public static final String POWER_SHORTAGE_ERROR_START_RULE = "POWER_SHORTAGE_ERROR_START_RULE";
	public static final String POWER_SHORTAGE_ERROR_START_NONE_AFFECTED_RULE = "POWER_SHORTAGE_ERROR_START_NONE_AFFECTED_RULE";
	public static final String POWER_SHORTAGE_ERROR_START_REQUEST_TRANSFER_RULE = "POWER_SHORTAGE_ERROR_START_REQUEST_TRANSFER_RULE";
	public static final String HANDLE_POWER_SHORTAGE_RULE = "HANDLE_POWER_SHORTAGE_RULE";
	public static final String HANDLE_POWER_SHORTAGE_TRANSFER_RULE = "HANDLE_POWER_SHORTAGE_TRANSFER_RULE";
	public static final String LISTEN_FOR_POWER_SHORTAGE_FINISH_RULE = "LISTEN_FOR_POWER_SHORTAGE_FINISH_RULE";
	public static final String LISTEN_FOR_POWER_SHORTAGE_FINISH_HANDLER_RULE = "LISTEN_FOR_POWER_SHORTAGE_FINISH_HANDLER_RULE";
	public static final String CHECK_AFFECTED_JOBS_RULE = "CHECK_AFFECTED_JOBS_RULE";
	public static final String CHECK_SINGLE_AFFECTED_JOB_RULE = "CHECK_SINGLE_AFFECTED_JOB_RULE";

	/**
	 * SERVER ERROR RULES
	 */
	public static final String LISTEN_FOR_SERVER_ERROR_RULE = "LISTEN_FOR_SERVER_ERROR_RULE";
	public static final String LISTEN_FOR_SERVER_ERROR_HANDLER_RULE = "LISTEN_FOR_SERVER_ERROR_HANDLER_RULE";
	public static final String LISTEN_FOR_SERVER_ERROR_HANDLE_NEW_ALERT_RULE = "LISTEN_FOR_SERVER_ERROR_HANDLE_NEW_ALERT_RULE";
	public static final String LISTEN_FOR_SERVER_ERROR_HANDLE_FINISH_RULE = "LISTEN_FOR_SERVER_ERROR_HANDLE_FINISH_RULE";
	public static final String LISTEN_FOR_SERVER_ERROR_HANDLE_PUT_ON_HOLD_RULE = "LISTEN_FOR_SERVER_ERROR_HANDLE_PUT_ON_HOLD_RULE";

	/**
	 * WEATHER DROP RULES
	 */
	public static final String WEATHER_DROP_ERROR_RULE = "WEATHER_DROP_ERROR_RULE";
	public static final String HANDLE_WEATHER_DROP_START_RULE = "HANDLE_WEATHER_DROP_START_RULE";
	public static final String HANDLE_WEATHER_DROP_FINISH_RULE = "HANDLE_WEATHER_DROP_FINISH_RULE";

	/**
	 * ENERGY RE-SUPPLY RULES
	 */
	public static final String LISTEN_FOR_SERVER_RE_SUPPLY_RULE = "LISTEN_FOR_SERVER_RE_SUPPLY_RULE";
	public static final String LISTEN_FOR_SERVER_RE_SUPPLY_HANDLER_RULE = "LISTEN_FOR_SERVER_RE_SUPPLY_HANDLER_RULE";
	public static final String RESUPPLY_JOB_WITH_GREEN_POWER_RULE = "RESUPPLY_JOB_WITH_GREEN_POWER_RULE";

	/**
	 * JOB DIVISION RULES
	 */
	public static final String PROCESS_JOB_NEW_INSTANCE_CREATION_RULE = "PROCESS_JOB_NEW_INSTANCE_CREATION_RULE";
	public static final String PROCESS_JOB_DIVISION_RULE = "PROCESS_JOB_DIVISION_RULE";
	public static final String PROCESS_JOB_SUBSTITUTION_RULE = "PROCESS_JOB_SUBSTITUTION_RULE";

	/**
	 * NEW JOB RULES
	 */
	public static final String NEW_JOB_RECEIVER_RULE = "NEW_JOB_RECEIVER_RULE";
	public static final String NEW_JOB_RECEIVER_HANDLER_RULE = "NEW_JOB_RECEIVER_HANDLER_RULE";
	public static final String NEW_JOB_RECEIVER_HANDLE_NO_AGENTS_RULE = "NEW_JOB_RECEIVER_HANDLE_NO_AGENTS_RULE";
	public static final String NEW_JOB_RECEIVER_HANDLE_NO_RESOURCES_RULE = "NEW_JOB_RECEIVER_HANDLE_NO_RESOURCES_RULE";
	public static final String NEW_JOB_RECEIVER_HANDLE_QUEUE_LIMIT_RULE = "NEW_JOB_RECEIVER_HANDLE_QUEUE_LIMIT_RULE";
	public static final String NEW_JOB_RECEIVER_HANDLE_NEW_JOB_RULE = "NEW_JOB_RECEIVER_HANDLE_NEW_JOB_RULE";
	public static final String NEW_JOB_RECEIVER_HANDLE_JOB_DUPLICATE_RULE = "NEW_JOB_RECEIVER_HANDLE_JOB_DUPLICATE_RULE";
	public static final String NEW_JOB_POLLING_RULE = "NEW_JOB_POLLING_RULE";
	public static final String NEW_JOB_POLLING_HANDLE_NO_CLOUD_AGENTS_RULE = "NEW_JOB_POLLING_HANDLE_NO_CLOUD_AGENTS_RULE";
	public static final String NEW_JOB_POLLING_HANDLE_JOB_RULE = "NEW_JOB_POLLING_HANDLE_JOB_RULE";
	public static final String POLL_NEXT_JOB_RULE = "POLL_NEXT_JOB_RULE";
	public static final String COMPUTE_JOB_PRIORITY_RULE = "COMPUTE_JOB_PRIORITY_RULE";
	public static final String COMPUTE_PRICE_RULE = "COMPUTE_PRICE_RULE";
	public static final String COMPARE_EXECUTION_PROPOSALS = "COMPARE_EXECUTION_PROPOSALS";
	public static final String NEW_JOB_ANNOUNCEMENT_RULE = "NEW_JOB_ANNOUNCEMENT_RULE";
	public static final String NEW_JOB_ANNOUNCEMENT_HANDLE_JOB_RULE = "NEW_JOB_ANNOUNCEMENT_HANDLE_JOB_RULE";
	public static final String NEW_JOB_ANNOUNCEMENT_HANDLE_ADJUST_TIME_FRAMES_RULE = "NEW_JOB_ANNOUNCEMENT_HANDLE_ADJUST_TIME_FRAMES_RULE";
	public static final String NEW_JOB_ANNOUNCEMENT_HANDLE_ADJUST_DEADLINE_REACH_RULE = "NEW_JOB_ANNOUNCEMENT_HANDLE_ADJUST_DEADLINE_REACH_RULE";


	/**
	 * JOB PRICE UPDATE RULES
	 */
	public static final String FINAL_PRICE_RECEIVER_RULE = "FINAL_PRICE_RECEIVER_RULE";
	public static final String ESTIMATED_PRICE_RECEIVER_RULE = "ESTIMATED_PRICE_RECEIVER_RULE";
	public static final String JOB_ENERGY_PRICE_RECEIVER_RULE = "JOB_ENERGY_PRICE_RECEIVER_RULE";
	public static final String JOB_ENERGY_PRICE_RECEIVER_HANDLER_RULE = "JOB_ENERGY_PRICE_RECEIVER_HANDLER_RULE";

	/**
	 * JOB STATUS UPDATE RULES
	 */
	public static final String JOB_STATUS_RECEIVER_RULE = "JOB_STATUS_RECEIVER_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLER_RULE = "JOB_STATUS_RECEIVER_HANDLER_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_PROCESSING_RULE = "JOB_STATUS_RECEIVER_HANDLE_PROCESSING_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_SCHEDULED_RULE = "JOB_STATUS_RECEIVER_HANDLE_SCHEDULED_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_CONFIRM_RULE = "JOB_STATUS_RECEIVER_HANDLE_CONFIRM_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_STARTED_JOB_RULE = "JOB_STATUS_RECEIVER_HANDLE_STARTED_JOB_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_FINISHED_JOB_RULE = "JOB_STATUS_RECEIVER_HANDLE_FINISHED_JOB_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_FAILED_JOB_RULE = "JOB_STATUS_RECEIVER_HANDLE_FAILED_JOB_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_ON_HOLD_JOB_RULE = "JOB_STATUS_RECEIVER_HANDLE_ON_HOLD_JOB_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_BACK_UP_RULE = "JOB_STATUS_RECEIVER_HANDLE_BACK_UP_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_ON_GREEN_RULE = "JOB_STATUS_RECEIVER_HANDLE_ON_GREEN_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_DELAYED_RULE = "JOB_STATUS_RECEIVER_HANDLE_DELAYED_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_ACCEPTED_RULE = "JOB_STATUS_RECEIVER_HANDLE_ACCEPTED_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_POSTPONED_RULE = "JOB_STATUS_RECEIVER_HANDLE_POSTPONED_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_RE_SCHEDULED_RULE = "JOB_STATUS_RECEIVER_HANDLE_RE_SCHEDULED_RULE";
	public static final String JOB_STATUS_RECEIVER_HANDLE_UNKNOWN_STATUS_RULE = "JOB_STATUS_RECEIVER_HANDLE_UNKNOWN_STATUS_RULE";
	public static final String JOB_STATUS_CHECK_RULE = "JOB_STATUS_CHECK_RULE";
	public static final String JOB_STATUS_HANDLER_RULE = "JOB_STATUS_HANDLER_RULE";
	public static final String JOB_STATUS_HANDLE_NOT_FOUND_RULE = "JOB_STATUS_HANDLE_NOT_FOUND_RULE";
	public static final String JOB_STATUS_HANDLE_NOT_STARTED_RULE = "JOB_STATUS_HANDLE_NOT_STARTED_RULE";
	public static final String JOB_STATUS_HANDLE_STARTED_RULE = "JOB_STATUS_HANDLE_STARTED_RULE";

	/**
	 * JOB EXECUTION RULES
	 */
	public static final String LOOK_FOR_JOB_EXECUTOR_RULE = "LOOK_FOR_JOB_EXECUTOR_RULE";
	public static final String LOOK_FOR_JOB_EXECUTOR_HANDLE_FAILURE_RULE = "LOOK_FOR_JOB_EXECUTOR_HANDLE_FAILURE_RULE";
	public static final String PROPOSE_TO_EXECUTE_JOB_RULE = "PROPOSE_TO_EXECUTE_JOB_RULE";
	public static final String JOB_MANUAL_FINISH_RULE = "JOB_MANUAL_FINISH_RULE";
	public static final String JOB_MANUAL_FINISH_HANDLER_RULE = "JOB_MANUAL_FINISH_HANDLER_RULE";
	public static final String JOB_MANUAL_FINISH_HANDLE_IN_PROGRESS_RULE = "JOB_MANUAL_FINISH_HANDLE_IN_PROGRESS_RULE";
	public static final String JOB_MANUAL_FINISH_HANDLE_NON_EXECUTED_RULE = "JOB_MANUAL_FINISH_HANDLE_NON_EXECUTED_RULE";
	public static final String START_JOB_EXECUTION_RULE = "START_JOB_EXECUTION_RULE";
	public static final String PROCESS_START_JOB_EXECUTION_RULE = "PROCESS_START_JOB_EXECUTION_RULE";
	public static final String FINISH_JOB_EXECUTION_RULE = "FINISH_JOB_EXECUTION_RULE";
	public static final String PROCESS_FINISH_JOB_EXECUTION_RULE = "PROCESS_FINISH_JOB_EXECUTION_RULE";
	public static final String PROCESS_FINISH_JOB_BACK_UP_EXECUTION_RULE = "PROCESS_FINISH_JOB_BACK_UP_EXECUTION_RULE";
	public static final String PROCESS_SCHEDULE_POWER_SUPPLY_RULE = "PROCESS_SCHEDULE_POWER_SUPPLY_RULE";
	public static final String PROCESS_SCHEDULE_POWER_SUPPLY_NO_RESOURCES_RULE = "PROCESS_SCHEDULE_POWER_SUPPLY_NO_RESOURCES_RULE";
	public static final String PROCESS_SCHEDULE_POWER_SUPPLY_CONFIRM_RULE = "PROCESS_SCHEDULE_POWER_SUPPLY_CONFIRM_RULE";
	public static final String INSUFFICIENT_RESOURCES_RULE = "INSUFFICIENT_RESOURCES_RULE";
	public static final String HANDLE_DELAYED_JOB_RULE = "HANDLE_DELAYED_JOB_RULE";
	public static final String HANDLE_JOB_STATUS_CHECK_RULE = "HANDLE_JOB_STATUS_CHECK_RULE";

	/**
	 * WEATHER RULES
	 */
	public static final String CHECK_WEATHER_FOR_NEW_POWER_SUPPLY_RULE = "CHECK_WEATHER_FOR_NEW_POWER_SUPPLY_RULE";
	public static final String CHECK_WEATHER_FOR_POWER_SHORTAGE_FINISH_RULE = "CHECK_WEATHER_FOR_POWER_SHORTAGE_FINISH_RULE";
	public static final String CHECK_WEATHER_FOR_RE_SUPPLY_RULE = "CHECK_WEATHER_FOR_RE_SUPPLY_RULE";
	public static final String CHECK_WEATHER_PERIODICALLY_RULE = "CHECK_WEATHER_PERIODICALLY_RULE";
	public static final String SCHEDULE_CHECK_WEATHER_PERIODICALLY_RULE = "SCHEDULE_CHECK_WEATHER_PERIODICALLY_RULE";
	public static final String NOT_ENOUGH_ENERGY_FOR_JOB_RULE = "NOT_ENOUGH_ENERGY_FOR_JOB_RULE";

	/**
	 * AGENTS STATUS CHANGE RULES
	 */
	public static final String SERVER_STATUS_CHANGE_RULE = "SERVER_STATUS_CHANGE_RULE";
	public static final String SERVER_STATUS_CHANGE_HANDLER_RULE = "SERVER_STATUS_CHANGE_HANDLER_RULE";
	public static final String SERVER_STATUS_CHANGE_HANDLE_NOT_FOUND_RULE = "SERVER_STATUS_CHANGE_HANDLE_NOT_FOUND_RULE";
	public static final String SERVER_STATUS_CHANGE_HANDLE_CHANGE_RULE = "SERVER_STATUS_CHANGE_HANDLE_CHANGE_RULE";
	public static final String GREEN_SOURCE_STATUS_CHANGE_RULE = "GREEN_SOURCE_STATUS_CHANGE_RULE";
	public static final String GREEN_SOURCE_STATUS_CHANGE_HANDLER_RULE = "GREEN_SOURCE_STATUS_CHANGE_HANDLER_RULE";
	public static final String GREEN_SOURCE_STATUS_CHANGE_HANDLE_DEACTIVATE_RULE = "GREEN_SOURCE_STATUS_CHANGE_HANDLE_DEACTIVATE_RULE";
	public static final String GREEN_SOURCE_STATUS_CHANGE_HANDLE_DISCONNECT_RULE = "GREEN_SOURCE_STATUS_CHANGE_HANDLE_DISCONNECT_RULE";
	public static final String GREEN_SOURCE_STATUS_CHANGE_HANDLE_CONNECT_RULE = "GREEN_SOURCE_STATUS_CHANGE_HANDLE_CONNECT_RULE";

	/**
	 * SEARCH AGENTS RULES
	 */
	public static final String SEARCH_OWNED_AGENTS_RULE = "SEARCH_OWNED_AGENTS_RULE";
	public static final String SEARCH_OWNER_AGENT_RULE = "SEARCH_OWNER_AGENT_RULE";
	public static final String SUBSCRIBE_OWNED_AGENTS_SERVICE_RULE = "SUBSCRIBE_OWNED_AGENTS_SERVICE_RULE";
}
