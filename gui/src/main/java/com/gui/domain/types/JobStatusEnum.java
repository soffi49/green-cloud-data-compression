package com.gui.domain.types;

/**
 * Enum storing available job statuses displayed in GUI
 */
public enum JobStatusEnum {

    CREATED("CREATED"),
    PROCESSING("PROCESSING"),
    IN_PROGRESS("IN PROGRESS"),
    DELAYED("DELAYED"),
    FINISHED("FINISHED");

    private final String status;

    JobStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
