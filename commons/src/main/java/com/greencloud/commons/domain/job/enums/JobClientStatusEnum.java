package com.greencloud.commons.domain.job.enums;

/**
 * Enum storing available job statuses displayed in GUI
 */
public enum JobClientStatusEnum {

	CREATED("BEFORE SEND"),
	SCHEDULED("SCHEDULED"),
	PROCESSED("PROCESSED"),
	IN_PROGRESS("IN PROGRESS"),
	DELAYED("DELAYED"),
	FINISHED("FINISHED"),
	ON_BACK_UP("ON BACK UP"),
	ON_HOLD("ON HOLD"),
	FAILED("FAILED"),
	REJECTED("REJECTED");

	private final String status;

	JobClientStatusEnum(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
