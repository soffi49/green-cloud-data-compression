package com.greencloud.application.agents.client.behaviour.jobannouncement.listener.logs;

/**
 * Class contains all constants used in logging information in listener behaviours during job announcement process
 */
public class JobAnnouncementListenerLog {

	// JOB UPDATE LOG MESSAGES
	public static final String CLIENT_JOB_SCHEDULED_LOG = "My job has been scheduled for execution!";
	public static final String CLIENT_JOB_PROCESSED_LOG = "The execution of my job is being processed!";
	public static final String CLIENT_JOB_DELAY_LOG = "The execution of my job has some delay! :(";
	public static final String CLIENT_JOB_POSTPONE_LOG = "The execution of my job is being postponed.";
	public static final String CLIENT_JOB_BACK_UP_LOG = "My job is being executed using the back up power!";
	public static final String CLIENT_JOB_GREEN_POWER_LOG = "My job is again being executed using the green power!";
	public static final String CLIENT_JOB_ON_HOLD_LOG = "My job has been put on hold :(!";
	public static final String CLIENT_JOB_FAILED_LOG = "The execution of my job has failed :(";
	public static final String CLIENT_JOB_RESCHEDULED_LOG = "The time frames of my job has been adjusted.";
	public static final String CLIENT_UNKNOWN_UPDATE_LOG = "There is not handler that can process update {} of the job.";

}
