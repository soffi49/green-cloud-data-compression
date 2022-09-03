package agents.server.domain;

import java.util.EnumSet;
import java.util.Set;

import domain.job.JobStatusEnum;

/**
 * Enum describing available power sources for the server
 */
public enum ServerPowerSourceType {
	GREEN_ENERGY(EnumSet.of(JobStatusEnum.IN_PROGRESS, JobStatusEnum.ACCEPTED)),
	BACK_UP_POWER(EnumSet.of(JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY)),
	ALL(EnumSet.allOf(JobStatusEnum.class));

	private final Set<JobStatusEnum> jobStatuses;

	ServerPowerSourceType(Set<JobStatusEnum> jobStatuses) {
		this.jobStatuses = jobStatuses;
	}

	public Set<JobStatusEnum> getJobStatuses() {
		return jobStatuses;
	}
}
